package com.wboard.client;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.ipc.HBaseRPC;
import org.apache.hadoop.hbase.ipc.VersionedProtocol;

import com.wboard.client.ui.WBoard;
import com.wboard.common.model.DrawableObject;
import com.wboard.common.model.WUser;
import com.wboard.common.protocol.ServerProtocol;


public class WBoardClient implements ServerProtocol{
	private static final WUser USER = new WUser();
	
	private static WBoard wboard;
	Configuration conf;
	private final InetSocketAddress serverIsa;
	private final InetSocketAddress clientIsa;
	
	public WBoardClient(Configuration conf){
		this.conf = conf;
		System.out.println(conf);
		String serverHost = conf.get("wboard.server.host", "localhost");
		int serverPort = conf.getInt("wboard.server.port", 5000);
		
		serverIsa = new InetSocketAddress(serverHost, serverPort);
		
		String clientHost = conf.get("wboard.client.hostname");
		int clientPort = conf.getInt("wboard.client.port", 5001);
		
		clientIsa = new InetSocketAddress(clientHost, clientPort);
	}
	
	public static WUser getSessionUser(){	return USER;	}
	
	@Override
	public long getProtocolVersion(String arg0, long arg1) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean createRoom(String title) {
		// TODO Auto-generated method stub
		try {
			HBaseRPC.call(ServerProtocol.class.getMethod("createRoom", String.class), new Object[][]{{title}}, new InetSocketAddress []{serverIsa}, (Class<? extends VersionedProtocol>) ServerProtocol.class, WBoardClient.USER, conf);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		
		return false;
	}

	@Override
	public boolean createUser(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean joinRoom(int roomid, int userid) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void draw(int roomid, DrawableObject obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void erase(int roomid, int userid, int objid) {
		// TODO Auto-generated method stub
		
	}

	
	public static void main(String[] args) throws ClassNotFoundException {
		// args[0] = 0: creating a new room, 1: joining a room
		// args[1] = user name
		final int createOrJoin = Integer.parseInt(args[0]);
		
		WBoardClient.getSessionUser().setUsername(args[1]);
		
		if(createOrJoin == 0){
			WBoardClient.getSessionUser().setRole(WUser.Role.MANAGER);
		}else{
			WBoardClient.getSessionUser().setRole(WUser.Role.MEMBER);
		}
		
		
		Configuration conf = new Configuration();
		conf.addResource("client.xml");
		
		WBoardClient wc = new WBoardClient(conf);
		
		System.out.println(wc.createRoom("test"));
		
			
		
		
		// TODO: register user to server
		
		//wboard = new WBoard(WBoardClient.getSessionUser());	// 보드 생성
		//wboard.open();
	}
}