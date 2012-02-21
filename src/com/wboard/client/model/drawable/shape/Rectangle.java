package com.wboard.client.model.drawable.shape;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.model.drawable.Shape;
import com.wboard.client.util.Constants;
import com.wboard.common.WObject;

public class Rectangle extends Shape {

	private static final long serialVersionUID = 5700177394364534025L;

	public Rectangle(Point startPoint, Point endPoint,	Color lineColor, Color faceColor) {
		super(startPoint, endPoint, lineColor, faceColor);
	}

	@Override
	public void draw(GC gc) {
		if(!faceColor.equals(Constants.WHITE)){
			gc.fillRectangle(this.getStartPoint().x, this.getStartPoint().y, this.getWidth(), this.getHeight());
		}
		gc.drawRectangle(this.getStartPoint().x, this.getStartPoint().y, this.getWidth(), this.getHeight());		
	}

	@Override
	public long getOid() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void readObject(ObjectInputStream in) throws ClassNotFoundException,
			IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void writeObject(ObjectOutputStream out) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int compareTo(WObject o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
