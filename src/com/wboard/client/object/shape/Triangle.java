package com.wboard.client.object.shape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.Shape;
import com.wboard.client.util.SALAD;

public class Triangle extends Shape {

	private static final long serialVersionUID = 350481520914656798L;

	public Triangle(int oid, Point startPoint, Point endPoint, Color lineColor,	Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		if(!faceColor.equals(SALAD.WHITE)){
			gc.fillPolygon(new int[]{this.getStartPoint().x + this.getWidth() / 2,
					this.getStartPoint().y,
					this.getStartPoint().x,
					this.getEndPoint().y,
					this.getEndPoint().x,
					this.getEndPoint().y});
		}
		gc.drawPolygon(new int[]{this.getStartPoint().x + this.getWidth() / 2,
				this.getStartPoint().y,
				this.getStartPoint().x,
				this.getEndPoint().y,
				this.getEndPoint().x,
				this.getEndPoint().y});		
	}

}
