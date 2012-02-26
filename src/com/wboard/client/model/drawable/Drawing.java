/**
 * WObject 중 그리기(Drawing) 객체에 대한 클래스
 */

package com.wboard.client.model.drawable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;

import com.wboard.client.util.Constants;
import com.wboard.common.WObject;


public class Drawing extends WClientObject{

	private static final long serialVersionUID = -4354373214511330044L;
	
	private ArrayList<Point> drawingPath;	// Drawing 객체의 Point 를 저장하는 배열

	/* 생성자 */
	public Drawing(Point startPoint) {
		super(OBJTYPE.FREE_SHAPE, startPoint);
		this.drawingPath = new ArrayList<Point>();
	}

	/* WObject 클래스의 Object 설정 함수 오버라이딩  */
	public void setWObject(Point startPoint, Point endPoint){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.width = endPoint.x - startPoint.x;
		this.height = endPoint.y - startPoint.y;
		this.setBoundary();
	}

	/* Select & Move: Drawing 배열의 모든 포인트를 이동 */
	public void moveDrawing(int movingX, int movingY){
		int size = this.getPathSize();
		if(size > 1){
			for(int i = 0; i < size; i++){
				Point p = this.getDrawingPoint(i);
				p.x = p.x + movingX;
				p.y = p.y + movingY;
			}	
		}
	}

	/* Drawing 배열의 모든 Point 를 출력  */
	@Override
	public void draw(GC gc){
		int size = this.getPathSize();
		if(size > 1){
			for(int i = 1; i < size; i++){
				Point p1 = this.getDrawingPoint(i - 1);
				Point p2 = this.getDrawingPoint(i);
				gc.drawLine(p1.x, p1.y, p2.x, p2.y);
			}		
		}
	}

	/* Drawing 객체의 Area 를 계산 */
	public void computeArea(){
		int top = Constants.DUMP_MAX;
		int left = Constants.DUMP_MAX;
		int right = Constants.DUMP_MIN;
		int bottom = Constants.DUMP_MIN;

		for(int i = 0; i < this.getPathSize(); i++){
			int x = drawingPath.get(i).x;
			int y = drawingPath.get(i).y;

			if(x < left){
				left = x;
			}
			if(x > right){
				right = x;
			}
			if(y < top){
				top = y;
			}
			if(y > bottom){
				bottom = y;
			}
		}
		this.setWObject(new Point(left, top), new Point(right, bottom));	// 계산된 값으로 Drawing Object Area를 설정
	}
	
	public void setDrawingPath(ArrayList<Point> drawingPath) {
		this.drawingPath = drawingPath;
	}

	public ArrayList<Point> getDrawingPath() {
		return drawingPath;
	}

	/* Drawing 배열에 Point 저장 */
	public void addDrawingPath(Point p){
		drawingPath.add(p);
	}

	/* Drawing 배열 봔환 */
	private Point getDrawingPoint(int index){
		return drawingPath.get(index);
	}

	/* 배열의 길이 반환 */
	private int getPathSize(){
		return drawingPath.size();
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
