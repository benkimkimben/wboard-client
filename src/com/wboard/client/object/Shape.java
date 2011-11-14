/**
 * 모든 내장 객체(도형)에 대한 클래스
 */

package com.wboard.client.object;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.WClientObject;


public abstract class Shape extends WClientObject {

	private static final long serialVersionUID = 7280905329827249046L;

	public static enum SHAPETYPE {LINE, OVAL, TRIANGLE, RECTANGLE, ROUND_RECTANGLE, PENTAGON, HEXAGON, ARROW, LINE_STAR, STAR};

	protected SHAPETYPE shapeType;
	
	/* 생성자 */
	public Shape(Point startPoint, Point endPoint, Color lineColor, Color faceColor) {
		super(OBJTYPE.SHAPE, startPoint);
		this.objType = OBJTYPE.SHAPE;
		this.setWObject(endPoint);
		this.lineColor = lineColor;
		this.faceColor = faceColor;
	}

}
