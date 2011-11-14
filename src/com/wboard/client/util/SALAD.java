/**
 *  Salad Project 의 모든 Constant 를 기록하는 클래스
 *  
 *  */

package com.wboard.client.util;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;


public class SALAD {

	// Control Offset
	public static final int GUI_OFFSET = 10;
	public static final int ICON_SIZE = 95;
	public static final int MENU_HEIGHT = 30;
	public static final int CLERK_SIZE = 350;
	public static final int TABITEM_SIZE = 30;
	
	// Color
	public static final Color WHITE = Display.getCurrent().getSystemColor(SWT.COLOR_WHITE);
	public static final Color BLACK = Display.getCurrent().getSystemColor(SWT.COLOR_BLACK);
	
	// Line
	public static final int DEFAULT_LINE = 2;
	
	// Order WObj
	public static final String ONESTAP_F = "OneStap_F";
	public static final String ONESTAP_B = "OneStap_B";
	public static final String[] WOBJORDER = {ONESTAP_F, ONESTAP_B};
	public static final int WOBJORDER_SIZE = WOBJORDER.length;
	
	// Size Signal
	public static final String NONE = "None";
	public static final String MOVE = "Move";
	public static final String LEFT_UP = "LeftUp";
	public static final String LEFT_DOWN = "LeftDown";
	public static final String RIGHT_UP = "RightUp";
	public static final String RIGHT_DOWN = "RightDown";
	public static final String[] TRANSFORM = {LEFT_UP, LEFT_DOWN, RIGHT_UP, RIGHT_DOWN};

	// DUMP MAX & MIN
	public static final int DUMP_MAX = 10000;
	public static final int DUMP_MIN = 0;
	
	// userList Bounds
	public static final int ULISTTITLE_Y = 420;
	public static final int USERLIST_Y = 460;
	public static final int USERLIST_SIZE_Y = 285;
	
	public static final String SERVER = "10.250.65.54";
}
