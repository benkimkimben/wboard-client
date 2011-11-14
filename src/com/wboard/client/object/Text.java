package com.wboard.client.object;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class Text extends WClientObject {
	private static final long serialVersionUID = -8802266590277444708L;
	
	public static final int TEXT_HEIGHT_OFFSET = 28;
	public static final int TEXT_WIDTH_OFFSET = 20;
	
	public String fontType = "Arial";

	private String[] textContent = {"", ""};

	public Text(Point startPoint) {
		super(OBJTYPE.TEXT, startPoint);
	}

	public String[] getTextContent() {
		return textContent;
	}

	public void setTextContent(String[] textContent) {
		this.textContent = textContent;
	}

	public void setArea(){
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
			areaHeight = height *(-1); 
		}
	
		if(textContent.length != 0){
			areaHeight = textContent.length * TEXT_HEIGHT_OFFSET;
			int longgest = 0;
			for(int i = 0; i < textContent.length; i++){
				if(longgest < textContent[i].length()){
					longgest = textContent[i].length();
				}
			}
			areaWidth = (longgest * TEXT_WIDTH_OFFSET > width)? 
					longgest * TEXT_WIDTH_OFFSET: width;
		}
		this.boundary = new Rectangle(left, top, areaWidth, areaHeight);
	}
	
	public void draw(GC gc){
		if(textContent.length != 0){
			for(int i = 0; i < textContent.length; i++){
				gc.drawString(textContent[i], this.startPoint.x, this.startPoint.y + i * TEXT_HEIGHT_OFFSET);
			}
		}
	}
}
