/**
 * 캔버스에 그려진 객체에 대한 클래스
 */

package com.wboard.client.object;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import com.wboard.client.util.SALAD;
import com.wboard.WObject;


public abstract class WClientObject extends WObject{

	private static final long serialVersionUID = -2767536013214256124L;

	public static enum OBJTYPE {FREE_SHAPE, TEXT, SHAPE};

	public static final int RECT_SIZE = 10;
	
	protected int xCoord;		// X-coordinate of object
	protected int yCoord;		// Y-coordinate of object
	
	protected int width;			// 넓이
	protected int height;			// 높이
	
	protected Point startPoint;		// Left, Top Point
	protected Point endPoint;		// Right Bottom Point

	protected Rectangle boundary;		// 객체 영역
	
	// GUI Style 지원 변수
	protected Color lineColor; 
	protected Color faceColor;
	protected int lineWidth;
	protected int lineCap;
	
	protected OBJTYPE objType;
	
	
	public abstract void draw(GC gc);
	
	/* 생성자 */
	public WClientObject(OBJTYPE objType) {
		super();
		this.objType = objType;
		this.lineColor = SALAD.BLACK;
		this.faceColor = SALAD.WHITE;
		this.lineWidth = SALAD.DEFAULT_LINE;
		
		
		// 스타일 초기화
		this.lineCap = SWT.CAP_ROUND;
		this.lineWidth = SALAD.DEFAULT_LINE;		
	}
	
	public WClientObject(OBJTYPE objType, Point startPoint) {
		this(objType);
		this.startPoint = startPoint;
		this.endPoint = startPoint;
	}
	
	/* 객체 설정1: right bottom point */
	public void setWObject(Point endPoint){
		this.endPoint = endPoint;
		this.width = endPoint.x - startPoint.x;
		this.height = endPoint.y - startPoint.y;
		this.setBoundary();
	}
	
	/* 객체 설정2: width height */
	public void setWObject(int width, int height){
		this.width = width;
		this.height = height;
		this.endPoint.x = startPoint.x + width;
		this.endPoint.y = startPoint.y + height;
		this.setBoundary();
	}
	
	/* 객체 설정3: start point, end point */
	public void setWObject(Point startPoint, Point endPoint){
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.width = endPoint.x - startPoint.x;
		this.height = endPoint.y - startPoint.y;
		this.setBoundary();
	}

	public void setWObject(int sx, int sy, int ex, int ey){
		this.startPoint.x = sx;
		this.startPoint.y = sy;
		this.endPoint.x = ex;
		this.endPoint.y = ey;
		
		this.width = endPoint.x - startPoint.x;
		this.height = endPoint.y - startPoint.y;
		this.setBoundary();
	}
	
	/* 기존 객체 정보로 객체의 영역 계산 */
	protected void setBoundary(){
		int left = startPoint.x;
		int top = startPoint.y;
		int areaWidth = width;
		int areaHeight = height;
		
		if(startPoint.x > endPoint.x){
			left = endPoint.x;
			areaWidth = width * (-1);
		}
		
		if(startPoint.y > endPoint.y){
			top = endPoint.y;
			areaHeight = height * (-1);
		}
		
		this.boundary = new Rectangle(left, top, areaWidth, areaHeight);
	}

	public Point getStartPoint() {
		return startPoint;
	}

	public void setStartPoint(Point startPoint) {
		this.startPoint = startPoint;
	}

	public Point getEndPoint() {
		return endPoint;
	}

	public void setEndPoint(Point endPoint) {
		this.endPoint = endPoint;
	}

	public Rectangle getBoundary() {
		return boundary;
	}

	public void setArea(Rectangle boundary) {
		this.boundary = boundary;
	}

	public Color getLineColor() {
		return lineColor;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public Color getFaceColor() {
		return faceColor;
	}

	public void setFaceColor(Color faceColor) {
		this.faceColor = faceColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		this.lineWidth = lineWidth;
	}


	public static int getRectSize() {
		return RECT_SIZE;
	}

	public int getxCoord() {
		return xCoord;
	}

	public void setxCoord(int xCoord) {
		this.xCoord = xCoord;
	}

	public int getyCoord() {
		return yCoord;
	}

	public void setyCoord(int yCoord) {
		this.yCoord = yCoord;
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public OBJTYPE getObjType() {
		return objType;
	}

	public void setLineCap(int lineCap) {
		this.lineCap = lineCap;
	}

	public int getLineCap() {
		return lineCap;
	}
	
	
}
