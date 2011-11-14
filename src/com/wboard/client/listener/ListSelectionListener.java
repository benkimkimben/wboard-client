package com.wboard.client.listener;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;

import com.wboard.client.ui.WBoard;


public class ListSelectionListener implements Listener {
	private WBoard wBoard;
	
	public ListSelectionListener(WBoard wBoard){
		this.wBoard = wBoard;
	}
	
	@Override
	public void handleEvent(Event event) {
		List list = (List)event.widget;
		List userList = (List)event.widget;
		Canvas canvas = wBoard.getCanvas();
		int listHeight = list.getItemHeight() * list.getItemCount();
		int userListHeight = userList.getItemHeight() * userList.getItemCount();
		
		if(event.y > listHeight && list.getItemCount() != 0){
			list.deselectAll();
		}
		else if(event.y > userListHeight && userList.getItemCount() != 0){
			userList.deselectAll();
		}
		canvas.redraw();
	}
}
