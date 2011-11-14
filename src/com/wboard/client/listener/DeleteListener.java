package com.wboard.client.listener;

import org.eclipse.swt.SWT;
//import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


import com.wboard.client.object.WClientObject;
import com.wboard.client.ui.WBoard;

//import com.wboard.client.util.SALAD;

public class DeleteListener implements Listener {

	private WBoard wBoard;

	public DeleteListener(WBoard wBoard){
		this.wBoard = wBoard;
	}

	@Override
	public void handleEvent(Event event) {

		if(event.type == SWT.KeyDown){
			if(event.keyCode == 127 && wBoard.getSelected() != null){
				Rectangle rect = null;
				for(int i = 0; i < wBoard.getWObjectList().size(); i++){
					WClientObject wcObject = wBoard.getWObjectList().get(i);
					if(wBoard.getSelected() == wcObject){
						rect = wcObject.getBoundary();
						wBoard.getWObjectList().remove(i);
						wBoard.setSelected(null);
					}
				}
				Canvas canvas = (Canvas)event.widget;
				canvas.redraw(rect.x - (WClientObject.RECT_SIZE), 
						rect.y - (WClientObject.RECT_SIZE),
						rect.width + WClientObject.RECT_SIZE * 3, rect.height + WClientObject.RECT_SIZE * 3, false);			
			}
		}
	}
}
