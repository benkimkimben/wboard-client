/**
 * 그리기 객체를 화면에 그리고 생성하는 리스너 
 */

package com.wboard.client.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


import com.wboard.client.object.Drawing;
import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;



public class DrawingListener implements Listener {

	private WBoard wBoard;
	Drawing drawing ;
	private int lastX;	// 이전 좌표 저장
	private int lastY;	// 이전 좌표 저장
	
	/* 생성자 */
	public DrawingListener(WBoard wBoard){
		this.wBoard = wBoard;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(wBoard.getMode() == MODE.FREE_SHAPE){
			Canvas canvas = (Canvas)event.widget;
			Cursor cursor = new Cursor(event.display, SWT.CURSOR_UPARROW);	// 그리기 모드로 커서 변경
			canvas.setCursor(cursor);
			
			GC gc = new GC(canvas); 
			
			switch(event.type){
				case SWT.MouseDown:	
					lastX = event.x;	// 초기화
					lastY = event.y;
					canvas.redraw();
					drawing = new Drawing(new Point(event.x, event.y));	// 시작점에서 그리기객체 생성
					break;	
					
				case SWT.MouseMove:
					if((event.stateMask & SWT.BUTTON1) == 0) break;
					
					// show drawing progress actively to the user
					gc.setForeground(wBoard.getFaceColor());
					gc.setLineWidth(wBoard.getLineWidth());
					gc.setLineCap(wBoard.getLineCap());
					gc.drawLine(lastX, lastY, event.x, event.y);	// 이전 좌표와 현 Event 좌표만큼 Line 그리기
					
					// add drawing path to the drawing object
					drawing.addDrawingPath(new Point(event.x, event.y));
					
					lastX = event.x;	// 좌표 갱신
					lastY = event.y;
					break;
					
				case SWT.MouseUp:

					if(drawing.getDrawingPath().size() < 5){break;}
					
					// when drawing is done set its properties 
					drawing.setWObject(new Point(event.x, event.y));
					drawing.computeArea();	// 그리기 객체의 영역 계산	
					drawing.setLineWidth(wBoard.getLineWidth());
					drawing.setFaceColor(wBoard.getFaceColor());
					drawing.setLineColor(wBoard.getLineColor());
					drawing.setLineCap(wBoard.getLineCap());
					
					wBoard.addWClientObject(drawing);
				
					break;
			}
			gc.dispose();
		}
	}
}
