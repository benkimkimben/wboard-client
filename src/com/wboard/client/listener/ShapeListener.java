/**
 * 내장 객체(도형) 그리기를 담당하는 리스너
 * */
package com.wboard.client.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;


import com.wboard.client.object.Shape;
import com.wboard.client.object.Shape.SHAPETYPE;
import com.wboard.client.object.shape.Arrow;
import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;
import com.wboard.client.util.SALAD;


public class ShapeListener implements Listener {
	// set shape type
	private SHAPETYPE shapeType;
	private WBoard wBoard;
	private static boolean drawShape;	// 새로운 객체를 그리고 있는 지를 나타내는 flag

	/* 도형 객체 설정에 필요한 좌표 저장 */
	private static int startX;
	private static int startY;
	private static int endX;
	private static int endY;
	private static int width;
	private static int height;

	/* 생성자 */
	public ShapeListener(WBoard wBoard, SHAPETYPE shapeType){
		this.wBoard = wBoard;
		this.shapeType = shapeType;
	}

	@Override
	public void handleEvent(Event event) {
		if(wBoard.getMode().equals(MODE.SHAPE)){
			Canvas canvas = wBoard.getCanvas();
			Cursor cursor = new Cursor(event.display, SWT.CURSOR_CROSS);	// 도형 그리기 모드로 커서 설정
			canvas.setCursor(cursor);

			GC gc = new GC(canvas);
			gc.setLineWidth(wBoard.getLineWidth());

			switch(event.type){
			case SWT.MouseDown:
				startX = event.x;	// 시작점 저장
				startY = event.y;
				canvas.redraw();
				break;

			case SWT.MouseMove:
				if((event.stateMask & SWT.BUTTON1) == 0) break;
				drawShape = true;	// 도형 그리기 모드 설정

				canvas.redraw();

				endX = event.x;	// 영역의 마지막 점 갱신
				endY = event.y;

				width = endX - startX;	// 높이, 넓이 계산 
				height = endY - startY;
				break;

			case SWT.Paint:
				if(!drawShape) break;	// 그리기 모드가 아닌 경우 break

				if(wBoard.getLineColor()!= null){
					gc.setForeground(wBoard.getLineColor());
				}
				if(wBoard.getFaceColor()!= null){
					gc.setBackground(wBoard.getFaceColor());
				}
				gc.setLineWidth(wBoard.getLineWidth());
				gc.setLineCap(wBoard.getLineCap());
				
				/* 각 도형의 스타일에 맞게 사용자 화면에 실시간으로 그림을 그림*/
				if(shapeType == SHAPETYPE.LINE){
					gc.drawLine(startX, startY, endX, endY);
				}
				else if(shapeType == SHAPETYPE.OVAL){
					if(wBoard.getFaceColor()!= null && !wBoard.getFaceColor().equals(SALAD.WHITE)){	// WHITE 일때 투명
						gc.fillOval(startX, startY, width, height);						
					}
					gc.drawOval(startX, startY, width, height);
				}
				else if(shapeType == SHAPETYPE.RECTANGLE){
					if(wBoard.getFaceColor()!=null && !wBoard.getFaceColor().equals(SALAD.WHITE)){
						gc.fillRectangle(startX, startY, width, height);
					}
					gc.drawRectangle(startX, startY, width, height);
				}
				else if(shapeType == SHAPETYPE.TRIANGLE){
					if(wBoard.getFaceColor()!=null  && !wBoard.getFaceColor().equals(SALAD.WHITE)){
						gc.fillPolygon(new int[]{startX + width / 2, startY,	// 삼각형을 그리기 위한 int array
								startX, endY, endX, endY});
					}
					gc.drawPolygon(new int[]{startX + width / 2, startY,	// 삼각형을 그리기 위한 int array
							startX, endY, endX, endY});
				}

				else if(shapeType == SHAPETYPE.ARROW){
					if(wBoard.getFaceColor()!=null && !wBoard.getFaceColor().equals(SALAD.WHITE)){
						gc.fillPolygon(new int[]{startX, startY + height/3,
								startX,	startY + (2*height)/3,
								startX + (2*width)/3, startY + (2*height)/3,
								startX + (2*width)/3, startY + height,
								startX + width, startY + height/2,
								startX + (2*width)/3, startY,
								startX + (2*width)/3, startY + height/3});
					}
					gc.drawPolygon(new int[]{startX, startY + height/3,
							startX,	startY + (2*height)/3,
							startX + (2*width)/3, startY + (2*height)/3,
							startX + (2*width)/3, startY + height,
							startX + width, startY + height/2,
							startX + (2*width)/3, startY,
							startX + (2*width)/3, startY + height/3});
				}


				drawShape = false;	// 그리기가 끝난 후 그리기 모두 해체
				break;
				
			case SWT.MouseUp:
				Shape shape = null;
				switch(shapeType){
					case ARROW: 
						shape = new Arrow(new Point(startX, startY), new Point(endX, endY), wBoard.getLineColor(), wBoard.getFaceColor());
					//TODO: add more shape
				}
				if(shape != null){
					shape.setLineWidth(wBoard.getLineWidth());
					wBoard.addWClientObject(shape);
				}
				break;
			}	
			gc.dispose();
		}
	}
}
