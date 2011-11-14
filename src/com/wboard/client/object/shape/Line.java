package com.wboard.client.object.shape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.Shape;

public class Line extends Shape {

	private static final long serialVersionUID = -8139956129550696127L;

	public static final int[]LINE_WIDTH = new int[]{1, 2, 3, 4, 5,6,7,8,9};	

	public Line(int oid, Point startPoint, Point endPoint, Color lineColor,	Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		gc.drawLine(this.getStartPoint().x, this.getStartPoint().y, this.getEndPoint().x, this.getEndPoint().y);
	}

}
