package com.wboard.client.util;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.ColorDialog;

import com.wboard.client.object.WClientObject;
import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;



public class ColorSetting{
	private MODE mode;
	private WClientObject wcObject;

	private Color color;
	private RGB rgb;
	private ColorDialog colordialog;

	public ColorSetting(WBoard wBoard){
		this.mode = wBoard.getMode();

		if(mode.equals(MODE.LINE_COLOR) || mode.equals(MODE.FACE_COLOR)){
			colordialog = new ColorDialog(wBoard.getBoardGUI().getMainShell());
			colordialog.setRGB(rgb);
			rgb = colordialog.open();	// 색 선택창열기
			if(rgb != null){			// 색선택 창에서 그냥 닫았을경우 널포인터 방지
				color = new Color(wBoard.getBoardGUI().getMainShell().getDisplay(), rgb);	//선택한 색 지정
			}
			else{
				return;	
			}
			
			Canvas canvas = wBoard.getCanvas();
			wBoard.setMode(wBoard.getPrevMode());	// 이전 모드로 변경
			
			if( wBoard.getSelected() == null){
				if(mode.equals(MODE.LINE_COLOR)){
					wBoard.setLineColor(color);
				}
				else if (mode.equals(MODE.FACE_COLOR)){
					wBoard.setFaceColor(color);
				}
				return;
			}
			else{
				wcObject =  wBoard.getSelected();
			}
			
			if( wBoard.getSelected() == null){
				return;
			}
			else{
				wcObject =  wBoard.getSelected();
			}	
			
			// 선택한 객체에대해서 색을 바꾸기
			if(mode.equals(MODE.LINE_COLOR)){	// 선색
				wcObject.setLineColor(color);
			}
			else if(mode.equals(MODE.FACE_COLOR)){	// 면색
				wcObject.setFaceColor(color);				
			}
			canvas.redraw();
		}
	}
}
