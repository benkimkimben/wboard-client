package com.wboard.client;

import com.wboard.client.ui.WBoard;
import com.wboard.user.User;


public class WClient {
	private final static User USER = new User();
	
	private static WBoard wboard;
	
	public static User getSessionUser(){	return USER;	}
	
	public static void main(String[] args) {
		// args[0] = 0: creating a new room, 1: joining a room
		// args[1] = user name
		final int createOrJoin = Integer.parseInt(args[0]);
		
		USER.setUsername(args[1]);
		
		if(createOrJoin == 0){
			USER.setRole(User.Role.MANAGER);
		}else{
			USER.setRole(User.Role.MEMBER);
		}
		// TODO: register user to server
		
				
		wboard = new WBoard(USER);	// 보드 생성
		wboard.open();
	}
}