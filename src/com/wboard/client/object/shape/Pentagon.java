package com.wboard.client.object.shape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.Shape;
import com.wboard.client.util.SALAD;

public class Pentagon extends Shape {

	private static final long serialVersionUID = 363973578827552686L;

	public Pentagon(int oid, Point startPoint, Point endPoint, Color lineColor,	Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		if(!faceColor.equals(SALAD.WHITE)){
			gc.fillPolygon(new int[]{this.getStartPoint().x, this.getStartPoint().y + this.getHeight()/3,
					this.getStartPoint().x + this.getWidth()/5, this.getEndPoint().y,
					this.getStartPoint().x + (4*this.getWidth())/5, this.getEndPoint().y,
					this.getEndPoint().x, this.getStartPoint().y + this.getHeight()/3,
					this.getStartPoint().x + this.getWidth()/2, this.getStartPoint().y});
		}
		gc.drawPolygon(new int[]{this.getStartPoint().x, this.getStartPoint().y + this.getHeight()/3,
				this.getStartPoint().x + this.getWidth()/5, this.getEndPoint().y,
				this.getStartPoint().x + (4*this.getWidth())/5, this.getEndPoint().y,
				this.getEndPoint().x, this.getStartPoint().y + this.getHeight()/3,
				this.getStartPoint().x + this.getWidth()/2, this.getStartPoint().y});		
	}

}
