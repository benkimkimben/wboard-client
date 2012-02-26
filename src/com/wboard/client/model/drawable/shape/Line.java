package com.wboard.client.model.drawable.shape;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.model.drawable.Shape;
import com.wboard.common.WObject;

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

	@Override
	public long getOid() {
		// TODO Auto-generated method stub
		return 0;
	}



	@Override
	public int compareTo(WObject o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void readFields(DataInput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void write(DataOutput arg0) throws IOException {
		// TODO Auto-generated method stub
		
	}

}
