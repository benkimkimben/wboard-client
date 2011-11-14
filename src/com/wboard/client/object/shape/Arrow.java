package com.wboard.client.object.shape;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.object.Shape;
import com.wboard.client.util.SALAD;

public class Arrow extends Shape {

	private static final long serialVersionUID = 2486243548066614204L;

	public Arrow(Point startPoint, Point endPoint, Color lineColor,	Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		if(!faceColor.equals(SALAD.WHITE)){
			gc.fillPolygon(new int[]{this.getStartPoint().x, this.getStartPoint().y+this.getHeight()/3,
						this.getStartPoint().x,	this.getStartPoint().y+(2*this.getHeight())/3,
						this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+(2*this.getHeight())/3,
						this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+this.getHeight(),
						this.getStartPoint().x+this.getWidth(), this.getStartPoint().y+this.getHeight()/2,
						this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y,
						this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+this.getHeight()/3});
		}
		gc.drawPolygon(new int[]{this.getStartPoint().x, this.getStartPoint().y+this.getHeight()/3,
				this.getStartPoint().x,	this.getStartPoint().y+(2*this.getHeight())/3,
				this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+(2*this.getHeight())/3,
				this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+this.getHeight(),
				this.getStartPoint().x+this.getWidth(), this.getStartPoint().y+this.getHeight()/2,
				this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y,
				this.getStartPoint().x+(2*this.getWidth())/3, this.getStartPoint().y+this.getHeight()/3});		
	}

}
