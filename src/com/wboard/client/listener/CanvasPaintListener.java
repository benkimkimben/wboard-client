/**
 * 현 캔버스의 모든 객체를 다시 그려주는 리스너
 * Paint Event 를 감지
 */

package com.wboard.client.listener;

import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import com.wboard.client.ui.WBoard;


public class CanvasPaintListener implements PaintListener {
	
	private WBoard wBoard;
	
	public CanvasPaintListener(WBoard wBoard){
		this.wBoard = wBoard;
	}
	
	@Override
	public void paintControl(PaintEvent event) {
		wBoard.redraw();
	}
}
