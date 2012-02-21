/**
 * 객체 선택 모드에서의 동작을 담당하는 리스너
 */

package com.wboard.client.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wboard.client.model.drawable.Drawing;
import com.wboard.client.model.drawable.WClientObject;
import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;



public class SelectListener implements Listener {
	private WBoard wBoard;
	private WClientObject selected;	// 선택된 객체 저장
	private static int lastX;	// 이전 좌표 저장
	private static int lastY;

	public SelectListener(WBoard wBoard){
		this.wBoard = wBoard;
		this.selected = wBoard.getSelected();
	}

	@Override
	public void handleEvent(Event event) {
		if(wBoard.getMode()== MODE.SELECT){

			Canvas canvas = wBoard.getCanvas();
			Cursor cursor = new Cursor(event.display, SWT.CURSOR_ARROW);	// Select 모드 커서 설정
			canvas.setCursor(cursor);

			switch(event.type){
			case SWT.MouseDown:
				lastX = event.x;	// 초기화
				lastY = event.y;

				for(int i = wBoard.getWObjectList().size() - 1; i >= 0 ; i--){
					WClientObject wcObject = wBoard.getWObjectList().get(i);
										
					if(wcObject.getBoundary().contains(event.x, event.y)){
						selected = wcObject;		// 객체의 영역에 event 가 발생하면 가장 최근의 selected 부터 감지
						wBoard.setSelected(selected);
						break;
					}
				}

				canvas.redraw();
				break;

			case SWT.MouseMove:
				if((event.stateMask & SWT.BUTTON1) == 0) break;

				int movingX = event.x - lastX;	// 움직인 거리 계산 
				int movingY = event.y - lastY;

			
				selected.setWObject(new Point(selected.getStartPoint().x + movingX,	// 움직인 만큼 객체 이동
						selected.getStartPoint().y + movingY),
						new Point(selected.getEndPoint().x + movingX,
								selected.getEndPoint().y + movingY));
				if(selected instanceof Drawing){	// 그리기 객체인 경우 모든 Point 이동
					((Drawing)selected).moveDrawing(movingX, movingY);
				}
				
				lastX = event.x;	// 좌표 갱신
				lastY = event.y;
				canvas.redraw();
				
				break;
			}
		}
	}
}
