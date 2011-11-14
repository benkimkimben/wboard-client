/**
 *  캔버스의 동작 모드를 설정하는 리스너
 */

package com.wboard.client.listener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wboard.client.ui.WBoard;
import com.wboard.client.ui.WBoard.MODE;
import com.wboard.client.util.ColorSetting;



public class ModeSettingListener implements Listener{

	private WBoard wBoard;	
	public ModeSettingListener(WBoard wBoard){
		this.wBoard = wBoard;
	}
	
	@Override
	public void handleEvent(Event event) {
		if(event.type == SWT.Selection){
			MODE mode = MODE.valueOf(event.widget.getData().toString());
			
			if(mode == MODE.FREE_SHAPE){			// 그리기 모드
				wBoard.setMode(MODE.FREE_SHAPE);
			}
			else if(mode == MODE.SHAPE){		// 도형 삽입 모드
				wBoard.setMode(MODE.SHAPE);
			}
			else if(mode == MODE.SELECT){		// 객체 선택 모드
				wBoard.setMode(MODE.SELECT);
				wBoard.getBoardGUI().getItemList().deselectAll();			// 리스트의 선택 객체 해제
				wBoard.getCanvas().redraw();
			}
			else if(mode == MODE.FACE_COLOR){	// 배경색 지정 모드
				wBoard.setMode(MODE.FACE_COLOR);
				new ColorSetting(wBoard);
			}
			else if(mode == MODE.LINE_COLOR){	// 선색 지정 모드
				wBoard.setMode(MODE.LINE_COLOR);
				new ColorSetting(wBoard);
			}
			else if(mode == MODE.TEXT){		// 텍스트 박스 삽입 모드
				wBoard.setMode(MODE.TEXT);
			}
			else if(mode == MODE.LINE_WIDTH){	// 선 굵기 지정 모드
				wBoard.setMode(MODE.LINE_WIDTH);
			}

			if(!(mode == MODE.LINE_WIDTH) || mode == MODE.FACE_COLOR || mode == MODE.LINE_COLOR)
				wBoard.setPrevMode(wBoard.getMode());	// 이전 모드 저장
			
			if(mode == MODE.SHAPE || mode == MODE.FREE_SHAPE)
				wBoard.setSelected(null);			// 보드 선택 객체 해제
		}
	}
}