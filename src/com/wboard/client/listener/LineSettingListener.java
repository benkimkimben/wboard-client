package com.wboard.client.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;


public class LineSettingListener implements Listener {

	private WBoard wBoard;
	
	public LineSettingListener(WBoard wBoard){
		this.wBoard = wBoard;
	}

	@Override
	public void handleEvent(Event event) {
		if(wBoard.getMode().equals(MODE.LINE_WIDTH)){
			int lineWidth = Integer.parseInt(event.widget.getData().toString()); 
			wBoard.setLineWidth(lineWidth);
			
			if(wBoard.getSelected() != null){
				wBoard.getSelected().setLineWidth(lineWidth);
			}
			
			wBoard.getCanvas().redraw();
			wBoard.setMode(wBoard.getPrevMode());
		}
	}
}
