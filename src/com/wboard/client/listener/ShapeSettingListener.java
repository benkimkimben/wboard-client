/**
 * 내장객체(도형) 그리기 모드일 때 각 도형의 스타일을 지정하는 리스너
 */
package com.wboard.client.listener;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.wboard.client.object.Shape.SHAPETYPE;
import com.wboard.client.ui.WBoard;

/**
 * TODO: code
 * @author SKCCADMIN
 *
 */
public class ShapeSettingListener implements Listener{

	private WBoard wBoard;
	
	public ShapeSettingListener(WBoard wBoard){
		this.wBoard = wBoard;
	}
	// TODO: code
	@Override
	public void handleEvent(Event event) {
		SHAPETYPE shapeType = SHAPETYPE.valueOf(event.widget.getData().toString());
	}
}
