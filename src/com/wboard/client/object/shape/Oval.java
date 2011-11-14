package com.wboard.client.object.shape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.Shape;
import com.wboard.client.util.SALAD;

public class Oval extends Shape {

	private static final long serialVersionUID = -1361198335307913010L;

	public Oval(int oid, Point startPoint, Point endPoint, Color lineColor,	Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		if(!faceColor.equals(SALAD.WHITE)){
			gc.fillOval(this.getStartPoint().x, this.getStartPoint().y, 
					this.getWidth(), this.getHeight());
		}
		gc.drawOval(this.getStartPoint().x, this.getStartPoint().y, 
				this.getWidth(), this.getHeight());		
	}

}
