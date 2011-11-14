package com.wboard.client.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;


import com.wboard.client.object.WClientObject;
import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;
import com.wboard.client.util.SALAD;


public class TextListener implements Listener {

	private WBoard wBoard;
	private static boolean drawText;
	private static Text text;
	private static com.wboard.client.object.Text textBox;
	private static boolean isTextOpen;

	static int startX, startY, endX, endY;
	static int width = 120, height = 120;

	public TextListener(WBoard wBoard){
		this.wBoard = wBoard;
	}

	@Override
	public void handleEvent(Event event) {
		if(wBoard.getMode().equals(MODE.TEXT) && !isTextOpen){
			
			Canvas canvas = wBoard.getCanvas();
			text = new Text(canvas, SWT.MULTI | SWT.WRAP);
			text.addListener(SWT.FocusOut, this);
			text.setFont(wBoard.getBoardFont());
			text.setVisible(false);
			Cursor cursor = new Cursor(event.display, SWT.CURSOR_CROSS);	// 도형 그리기 모드로 커서 설정
			canvas.setCursor(cursor);

			GC gc = new GC(canvas);
			gc.setLineWidth(SALAD.DEFAULT_LINE);

			switch(event.type){
			case SWT.MouseDown:
				startX = event.x;	// 시작점 저장
				startY = event.y;
				endX = event.x;
				endY = event.y;
				canvas.redraw();
				break;

			case SWT.MouseMove:
				if((event.stateMask & SWT.BUTTON1) == 0) break;
				drawText = true;
				
				if(event.x < startX || event.y < startY){
					wBoard.setMode(MODE.SELECT);
					return;
				}

				Rectangle rect = new Rectangle(startX, startY, width, height);
				canvas.redraw(rect.x - (WClientObject.RECT_SIZE ), 
						rect.y - (WClientObject.RECT_SIZE ),
						rect.width + WClientObject.RECT_SIZE * 3, rect.height + WClientObject.RECT_SIZE * 3, false);

				endX = event.x;	// 영역의 마지막 점 갱신
				endY = event.y;

				width = endX - startX;	// 높이, 넓이 계산 
				height = endY - startY;

				break;
			case SWT.Paint:
				if(!drawText) break;
				Rectangle textRect = new Rectangle(startX, startY, width, height);
				gc.drawFocus(textRect.x, textRect.y, textRect.width, textRect.height);
				
				drawText = false;
				break;

			case SWT.MouseUp:
				text.setBounds(startX, startY, width, height);
				text.setCursor(new Cursor(event.display, SWT.CURSOR_IBEAM));
				text.setVisible(true);
				text.setFocus();
				gc.setLineStyle(SWT.LINE_DOT);
				gc.drawRectangle(text.getBounds());
				textBox = new com.wboard.client.object.Text(new Point(startX, startY));
				textBox.setWObject(new Point(startX, startY), new Point(endX, endY));

				isTextOpen = true;
				
				cursor = new Cursor(event.display, SWT.CURSOR_ARROW);	// Select 모드 커서 설정
				canvas.setCursor(cursor);
				
				wBoard.setSelected(null);
				break;
			}
			gc.dispose();
		}

		if(isTextOpen){
			switch(event.type){
			case SWT.MouseDown:
				wBoard.addWClientObject(textBox);

				Canvas canvas = wBoard.getCanvas();
				canvas.redraw();		
				
				String[] contents = text.getText().split("\n");
				textBox.setTextContent(contents);
				textBox.setWObject(text.getSize().x, text.getSize().y);
				canvas.redraw(startX - 1, startY - 1, width + 2, height + 2, false);
				
				GC gc = new GC(canvas);
				gc.setFont(wBoard.getBoardFont());
				textBox.draw(gc);
				gc.dispose();
				
				text.dispose();
				isTextOpen = false;
				if(wBoard.getMode().equals(MODE.TEXT)){
					wBoard.setMode(MODE.SELECT);
				}
				else{
					wBoard.setMode(wBoard.getMode());					
				}
			}
		}
	}
}
