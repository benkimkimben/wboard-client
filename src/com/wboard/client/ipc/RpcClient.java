package com.wboard.client.ipc;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import javax.net.SocketFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.util.PoolMap;
import org.apache.hadoop.hbase.util.PoolMap.PoolType;
import org.apache.hadoop.ipc.VersionedProtocol;
import org.apache.hadoop.net.NetUtils;

import com.wboard.client.ui.WBoard;
import com.wboard.common.WObject;
import com.wboard.common.conf.Configuration;
import com.wboard.common.model.User;
import com.wboard.common.protocol.ClientProtocol;

/** 
 * 
 * A client for an IPC service.  IPC calls take a single {@link WObject} as a
 * parameter, and return a {@link WObject} as their value.  A service runs on
 * a port and is defined by a parameter class and a value class.
 * 
 * @see org.apache.hadoop.hbase.ipc.HBaseClient
 */
public class RpcClient{
	/** A call waiting for a value. */
	protected class Call {
		final int id;                                       // call id
		final WObject param;                               // parameter
		WObject value;                               // value, null if error
		IOException error;                            // exception, null if value
		boolean done;                                 // true when call is done
		long startTime;

		protected Call(WObject param) {
			this.param = param;
			this.startTime = System.currentTimeMillis();
			synchronized (RpcClient.this) {
				this.id = counter++;
			}
		}

		/** Indicate when the call is complete and the
		 * value or error are available.  Notifies by default.  */
		protected synchronized void callComplete() {
			this.done = true;
			notify();                                 // notify caller
		}

		/** Set the exception when there is an error.
		 * Notify the caller the call is done.
		 *
		 * @param error exception thrown by the call; either local or remote
		 */
		public synchronized void setException(IOException error) {
			this.error = error;
			callComplete();
		}

		/** Set the return value when there is no error.
		 * Notify the caller the call is done.
		 *
		 * @param value return value of the call.
		 */
		public synchronized void setValue(WObject value) {
			this.value = value;
			callComplete();
		}

		public long getStartTime() {
			return this.startTime;
		}
	}


	/** Thread that reads responses and notifies callers.  Each connection owns a
	 * socket connected to a remote address.  Calls are multiplexed through this
	 * socket: responses may be delivered out of order. */
	protected class Connection extends Thread {
		private ConnectionHeader header;              // connection header
		protected ConnectionId remoteId;
		protected Socket socket = null;                 // connected socket
		protected DataInputStream in;
		protected DataOutputStream out;

		// currently active calls
		protected final ConcurrentSkipListMap<Integer, Call> calls = new ConcurrentSkipListMap<Integer, Call>();

		public Connection(ConnectionId remoteId) throws IOException {
			if (remoteId.getAddress().isUnresolved()) {
				throw new UnknownHostException("unknown host: " +
						remoteId.getAddress().getHostName());
			}
			this.remoteId = remoteId;
			User ticket = remoteId.getTicket();
			Class<? extends VersionedProtocol> protocol = remoteId.getProtocol();

			header = new ConnectionHeader(
					protocol == null ? null : protocol.getName(), ticket);

			this.setName("IPC Client (" + socketFactory.hashCode() +") connection to " +
					remoteId.getAddress().toString() +
					((ticket==null)?" from an unknown user": (" from " + ticket.getUsername())));
			this.setDaemon(true);
		}

		/**
		 * Add a call to this connection's call queue and notify
		 * a listener; synchronized.
		 * Returns false if called during shutdown.
		 * @param call to add
		 * @return true if the call was added.
		 */
		protected synchronized boolean addCall(Call call) {
			
			calls.put(call.id, call);
			notify();
			return true;
		}

		protected synchronized void setupConnection() throws IOException {

			while (true) {
				try {
					this.socket = socketFactory.createSocket();
					// connection time out is 20s
					this.socket.connect(remoteId.getAddress());
					
					return;
				} catch (SocketTimeoutException toe) { //TODO log
				} catch (IOException ie) {		//TODO log
				
				}
			}
		}

		/** Connect to the server and set up the I/O streams. It then sends
		 * a header to the server and starts
		 * the connection thread that waits for responses.
		 * @throws java.io.IOException e
		 */
		protected synchronized void setupIOstreams()
		throws IOException, InterruptedException {

			if (socket != null ) {
				return;
			}

			try {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Connecting to "+remoteId);
				}
				setupConnection();
				this.in = new DataInputStream(new BufferedInputStream(NetUtils.getInputStream(socket)));
				this.out = new DataOutputStream(new BufferedOutputStream(NetUtils.getOutputStream(socket)));
				writeHeader();

				// start the receiver thread after the socket connection has been set up
				start();
			} catch (IOException e) {
				markClosed(e);
				close();

				throw e;
			}
		}

		protected void closeConnection() {
			// close the current connection
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					LOG.warn("Not able to close a socket", e);
				}
			}
			// set socket to null so that the next call to setupIOstreams
			// can start the process of connect all over again.
			socket = null;
		}

	
		/* Write the header for each connection
		 * Out is not synchronized because only the first thread does this.
		 */
		private void writeHeader() throws IOException {
			out.write(HBaseServer.HEADER.array());
			out.write(HBaseServer.CURRENT_VERSION);
			//When there are more fields we can have ConnectionHeader Writable.
			DataOutputBuffer buf = new DataOutputBuffer();
			header.write(buf);

			int bufLen = buf.getLength();
			out.writeInt(bufLen);
			out.write(buf.getData(), 0, bufLen);
		}

		/* wait till someone signals us to start reading RPC response or
		 * it is idle too long, it is marked as to be closed,
		 * or the client is marked as not running.
		 *
		 * Return true if it is time to read a response; false otherwise.
		 */
		@SuppressWarnings({"ThrowableInstanceNeverThrown"})
		protected synchronized boolean waitForWork() {
			if (calls.isEmpty() && !shouldCloseConnection.get()  && running.get())  {
				long timeout = maxIdleTime-
				(System.currentTimeMillis()-lastActivity.get());
				if (timeout>0) {
					try {
						wait(timeout);
					} catch (InterruptedException ignored) {}
				}
			}

			if (!calls.isEmpty() && !shouldCloseConnection.get() && running.get()) {
				return true;
			} else if (shouldCloseConnection.get()) {
				return false;
			} else if (calls.isEmpty()) { // idle connection closed or stopped
				markClosed(null);
				return false;
			} else { // get stopped but there are still pending requests
				markClosed((IOException)new IOException().initCause(
						new InterruptedException()));
				return false;
			}
		}

		public SocketAddress getRemoteAddress() {
			return remoteId.getAddress();
		}


		@Override
		public void run() {
			if (LOG.isDebugEnabled())
				LOG.debug(getName() + ": starting, having connections "
						+ connections.size());

			try {
				while (waitForWork()) {//wait here for work - read or close connection
					receiveResponse();
				}
			} catch (Throwable t) {
				LOG.warn("Unexpected exception receiving call responses", t);
				markClosed(new IOException("Unexpected exception receiving call responses", t));
			}

			close();

			if (LOG.isDebugEnabled())
				LOG.debug(getName() + ": stopped, remaining connections "
						+ connections.size());
		}

		/* Initiates a call by sending the parameter to the remote server.
		 * Note: this is not called from the Connection thread, but by other
		 * threads.
		 */
		protected void sendParam(Call call) {
			if (shouldCloseConnection.get()) {
				return;
			}

			// For serializing the data to be written.

			final DataOutputBuffer d = new DataOutputBuffer();
			try {
				if (LOG.isDebugEnabled())
					LOG.debug(getName() + " sending #" + call.id);

				d.writeInt(0xdeadbeef); // placeholder for data length
				d.writeInt(call.id);
				call.param.write(d);
				byte[] data = d.getData();
				int dataLength = d.getLength();
				// fill in the placeholder
				Bytes.putInt(data, 0, dataLength - 4);
				//noinspection SynchronizeOnNonFinalField
				synchronized (this.out) { // FindBugs IS2_INCONSISTENT_SYNC
					out.write(data, 0, dataLength);
					out.flush();
				}
			} catch(IOException e) {
				markClosed(e);
			} finally {
				//the buffer is just an in-memory buffer, but it is still polite to
				// close early
				IOUtils.closeStream(d);
			}
		}

		/* Receive a response.
		 * Because only one receiver, so no synchronization on in.
		 */
		protected void receiveResponse() {
			if (shouldCloseConnection.get()) {
				return;
			}
			touch();

			try {
				// See HBaseServer.Call.setResponse for where we write out the response.
				// It writes the call.id (int), a flag byte, then optionally the length
				// of the response (int) followed by data.

				// Read the call id.
				int id = in.readInt();

				if (LOG.isDebugEnabled())
					LOG.debug(getName() + " got value #" + id);
				Call call = calls.remove(id);

				// Read the flag byte
				byte flag = in.readByte();
				boolean isError = ResponseFlag.isError(flag);
				if (ResponseFlag.isLength(flag)) {
					// Currently length if present is unused.
					in.readInt();
				}
				int state = in.readInt(); // Read the state.  Currently unused.
				if (isError) {
					if (call != null) {
						//noinspection ThrowableInstanceNeverThrown
						call.setException(new RemoteException(WritableUtils.readString(in),
								WritableUtils.readString(in)));
					}
				} else {
					Writable value = ReflectionUtils.newInstance(valueClass, conf);
					value.readFields(in);                 // read value
					// it's possible that this call may have been cleaned up due to a RPC
					// timeout, so check if it still exists before setting the value.
					if (call != null) {
						call.setValue(value);
					}
				}
			} catch (IOException e) {
				if (e instanceof SocketTimeoutException && remoteId.rpcTimeout > 0) {
					// Clean up open calls but don't treat this as a fatal condition,
					// since we expect certain responses to not make it by the specified
					// {@link ConnectionId#rpcTimeout}.
					closeException = e;
				} else {
					// Since the server did not respond within the default ping interval
					// time, treat this as a fatal condition and close this connection
					markClosed(e);
				}
			} finally {
				if (remoteId.rpcTimeout > 0) {
					cleanupCalls(remoteId.rpcTimeout);
				}
			}
		}

		protected synchronized void markClosed(IOException e) {
			if (shouldCloseConnection.compareAndSet(false, true)) {
				closeException = e;
				notifyAll();
			}
		}

		/** Close the connection. */
		protected synchronized void close() {
			if (!shouldCloseConnection.get()) {
				LOG.error("The connection is not in the closed state");
				return;
			}

			// release the resources
			// first thing to do;take the connection out of the connection list
			synchronized (connections) {
				connections.remove(remoteId, this);
			}

			// close the streams and therefore the socket
			IOUtils.closeStream(out);
			IOUtils.closeStream(in);

			// clean up all calls
			if (closeException == null) {
				if (!calls.isEmpty()) {
					LOG.warn(
					"A connection is closed for no cause and calls are not empty");

					// clean up calls anyway
					closeException = new IOException("Unexpected closed connection");
					cleanupCalls();
				}
			} else {
				// log the info
				if (LOG.isDebugEnabled()) {
					LOG.debug("closing ipc connection to " + remoteId.address + ": " +
							closeException.getMessage(),closeException);
				}

				// cleanup calls
				cleanupCalls();
			}
			if (LOG.isDebugEnabled())
				LOG.debug(getName() + ": closed");
		}

		/* Cleanup all calls and mark them as done */
		protected void cleanupCalls() {
			cleanupCalls(0);
		}

		protected void cleanupCalls(long rpcTimeout) {
			Iterator<Entry<Integer, Call>> itor = calls.entrySet().iterator();
			while (itor.hasNext()) {
				Call c = itor.next().getValue();
				long waitTime = System.currentTimeMillis() - c.getStartTime();
				if (waitTime >= rpcTimeout) {
					c.setException(closeException); // local exception
					synchronized (c) {
						c.notifyAll();
					}
					itor.remove();
				} else {
					break;
				}
			}
			try {
				if (!calls.isEmpty()) {
					Call firstCall = calls.get(calls.firstKey());
					long maxWaitTime = System.currentTimeMillis() - firstCall.getStartTime();
					if (maxWaitTime < rpcTimeout) {
						rpcTimeout -= maxWaitTime;
					}
				}
				if (!shouldCloseConnection.get()) {
					closeException = null;
					if (socket != null) {
						socket.setSoTimeout((int) rpcTimeout);
					}
				}
			} catch (SocketException e) {
				LOG.debug("Couldn't lower timeout, which may result in longer than expected calls");
			}
		}
	}
	/**
	 * This class holds the address and the user ticket. The client connections
	 * to servers are uniquely identified by <remoteAddress, ticket>
	 */
	protected static class ConnectionId {
		final InetSocketAddress address;
		final User ticket;
		final int rpcTimeout;
		Class<? extends VersionedProtocol> protocol;
		private static final int PRIME = 16777619;

		ConnectionId(InetSocketAddress address,
				Class<? extends VersionedProtocol> protocol,
				User ticket,
				int rpcTimeout) {
			this.protocol = protocol;
			this.address = address;
			this.ticket = ticket;
			this.rpcTimeout = rpcTimeout;
		}

		InetSocketAddress getAddress() {
			return address;
		}

		Class<? extends VersionedProtocol> getProtocol() {
			return protocol;
		}

		User getTicket() {
			return ticket;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConnectionId) {
				ConnectionId id = (ConnectionId) obj;
				return address.equals(id.address) && protocol == id.protocol &&
				((ticket != null && ticket.equals(id.ticket)) ||
						(ticket == id.ticket)) && rpcTimeout == id.rpcTimeout;
			}
			return false;
		}

		@Override  // simply use the default Object#hashcode() ?
		public int hashCode() {
			return (address.hashCode() + PRIME * (
					PRIME * System.identityHashCode(protocol) ^
					(ticket == null ? 0 : ticket.hashCode()) )) ^ rpcTimeout;
		}
	}


	private static final Log LOG = LogFactory.getLog("org.apache.hadoop.ipc.HBaseClient");
	protected final PoolMap<ConnectionId, Connection> connections;

	protected final Class<? extends WObject> valueClass;   // class of call values
	final protected Configuration conf;
	protected final SocketFactory socketFactory;           // how to create sockets
	protected int counter;                            // counter for call ids

	public RpcClient(Class<? extends WObject> valueClass, Configuration conf, SocketFactory factory) {
		this.valueClass = valueClass;
		this.socketFactory = factory;
		this.conf = conf;
		this.connections = new PoolMap<ConnectionId, Connection>(
				PoolType.ThreadLocal, Integer.MAX_VALUE);
	}

	/** 
	 * Make a call, passing <code>param</code>, to the IPC server running at
	 * <code>address</code>, returning the value.  Throws exceptions if there are
	 * network problems or if the remote code threw an exception.
	 */
	public WObject call(WObject param, InetSocketAddress address, int rpcTimeout)
	throws IOException {
		Call call = new Call(param);
		Connection connection = getConnection(addr, protocol, ticket, rpcTimeout, call);
		connection.sendParam(call);                 // send the parameter
		boolean interrupted = false;
		//noinspection SynchronizationOnLocalVariableOrMethodParameter
		synchronized (call) {
			while (!call.done) {
				try {
					call.wait();                           // wait for the result
				} catch (InterruptedException ignored) {
					// save the fact that we were interrupted
					interrupted = true;
				}
			}

			if (interrupted) {
				// set the interrupt flag now that we are done waiting
				Thread.currentThread().interrupt();
			}

			if (call.error != null) {
				if (call.error instanceof RemoteException) {
					call.error.fillInStackTrace();
					throw call.error;
				}
				// local exception
				throw wrapException(addr, call.error);
			}
			return call.value;
		}
	}
}