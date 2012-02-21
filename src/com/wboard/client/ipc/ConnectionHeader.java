/*
 * Copyright 2010 The Apache Software Foundation
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wboard.client.ipc;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.wboard.common.WObject;
import com.wboard.common.model.User;

/**
 * The IPC connection header sent by the client to the server
 * on connection establishment.
 */
class ConnectionHeader implements WObject {

	private static final long serialVersionUID = 1L;
	protected String protocol;

	public ConnectionHeader() {}

	/**
	 * Create a new {@link ConnectionHeader} with the given <code>protocol</code>
	 * and {@link User}.
	 * @param protocol protocol used for communication between the IPC client
	 *                 and the server
	 * @param user {@link User} of the client communicating with
	 *            the server
	 */
	public ConnectionHeader(String protocol, User user) {
		this.protocol = protocol;
	}
	
	public User getUser() {
		return null;
	}

	public String getProtocol() {
		return protocol;
	}


	public String toString() {
		return protocol;
	}

	@Override
	public int compareTo(WObject o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getOid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void readObject(ObjectInputStream in) throws ClassNotFoundException,
			IOException {
		protocol = in.readUTF();
	}

	@Override
	public void writeObject(ObjectOutputStream out) throws IOException {
		out.writeUTF((protocol == null) ? "" : protocol);
	}
}
