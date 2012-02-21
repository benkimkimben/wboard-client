/**
 * Salad Program 의 GUI를 담당하는 클래스
 */
package com.wboard.client.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;


import com.wboard.client.listener.CanvasPaintListener;
import com.wboard.client.listener.DeleteListener;
import com.wboard.client.listener.DrawingListener;
import com.wboard.client.listener.LineSettingListener;
import com.wboard.client.listener.ListSelectionListener;
import com.wboard.client.listener.ModeSettingListener;
import com.wboard.client.listener.SelectListener;
import com.wboard.client.listener.ShapeListener;
import com.wboard.client.listener.ShapeSettingListener;
import com.wboard.client.model.drawable.Drawing;
import com.wboard.client.model.drawable.Shape;
import com.wboard.client.model.drawable.WClientObject;
import com.wboard.client.model.drawable.Shape.SHAPETYPE;
import com.wboard.client.model.drawable.WClientObject.OBJTYPE;
import com.wboard.client.model.drawable.shape.Line;
import com.wboard.client.ui.WBoard.MODE;
import com.wboard.client.util.Constants;

public class BoardGUI {

	/* Static Variable */	
	// Display 
	public static final int DISPLAY_WIDTH = 1500;
	public static final int DISPLAY_HEIGHT = 800;
		
	// Main Shell
	public static final int MAIN_SHELL_WIDTH = DISPLAY_WIDTH - 150;
	public static final int MAIN_SHELL_HEIGHT = DISPLAY_HEIGHT - 30;
	public static final int MAIN_SHELL_X = 0;
	public static final int MAIN_SHELL_Y = 0;

	// GUI Controls
	private Display display;
	private Shell mainShell;

	// SWT GUI list 
	private List itemList;
	private List userList;
	
	


	public BoardGUI(){
		display = new Display();
		this.mainShell = new Shell(display, SWT.DIALOG_TRIM);

		// Main shell Setting
		mainShell.setBounds(MAIN_SHELL_X, MAIN_SHELL_Y,	MAIN_SHELL_WIDTH, MAIN_SHELL_HEIGHT);
		mainShell.setLayout(new RowLayout());
		mainShell.setText("WhiteBoard");
		

	}
	
	public void openShell(){
		mainShell.open();
		
		while(!mainShell.isDisposed()){		// main shell 이 죽지 않은 경우
			if(!display.readAndDispatch())
				display.sleep();	// event 가 발생하지 않은 경우 sleep
		}

		display.dispose();		// 자원 release
	}
	
	

	// 화이트 보드에 툴바 추가
	public void addToolbar(WBoard wBoard){	
		// 툴바 용 컴포짓
		Composite toolComposite = new Composite(mainShell, SWT.NONE);
		toolComposite.setBounds(MAIN_SHELL_X, MAIN_SHELL_Y, MAIN_SHELL_WIDTH, Constants.MENU_HEIGHT);
		// 툴바
		ToolBar toolBar = new ToolBar(toolComposite, SWT.HORIZONTAL | SWT.CENTER);
		toolBar.setBounds(toolComposite.getClientArea());

		addToolbarItem(wBoard, toolBar, MODE.SELECT.toString());		/* Select Mode */
		addToolbarItem(wBoard, toolBar, MODE.FREE_SHAPE.toString());		/* Draw Mode */

		/* Shape Mode */
		final Menu shapeMenu = new Menu(mainShell, SWT.POP_UP);	// 드롭다운 메뉴
		for(SHAPETYPE st: SHAPETYPE.values()){
			MenuItem item = new MenuItem(shapeMenu, SWT.PUSH);
			item.setText(st.toString());
			item.setData(st.toString());
			item.addListener(SWT.Selection, new ShapeSettingListener(wBoard));
		}
		addListToolbarItem(wBoard, toolBar, shapeMenu, MODE.SHAPE.toString());
		
		addToolbarItem(wBoard, toolBar, MODE.TEXT.toString());		/* Text Mode*/
		addToolbarItem(wBoard, toolBar, MODE.LINE_COLOR.toString());		/* Line Color Tool */ 
		addToolbarItem(wBoard, toolBar, MODE.FACE_COLOR.toString());		/* Face Color Tool */

		/* Line Width Tool  */
		final Menu lineWidthMenu = new Menu(mainShell, SWT.POP_UP);	// 드롭다운 메뉴
		for(int lt : Line.LINE_WIDTH){
			MenuItem item = new MenuItem(lineWidthMenu, SWT.PUSH);
			item.setText("Line"+lt);
			item.setData(Integer.toString(lt));
			item.addListener(SWT.Selection, new LineSettingListener(wBoard));
		}
		addListToolbarItem(wBoard, toolBar, lineWidthMenu, MODE.LINE_WIDTH.toString());
		
	}
	
	private void addToolbarItem(WBoard wBoard, ToolBar toolBar, String text){
		ToolItem toolItem = new ToolItem(toolBar, SWT.PUSH);
		toolItem.setData(text);
		toolItem.setText(text);
		toolItem.addListener(SWT.Selection, new ModeSettingListener(wBoard));
	}
	private void addListToolbarItem(WBoard wBoard, final ToolBar toolBar, final Menu menu, String text){
		final ToolItem toolItem = new ToolItem(toolBar, SWT.DROP_DOWN);
		toolItem.setData(text);
		toolItem.setText(text);
		toolItem.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if(event.detail == SWT.ARROW){
					Rectangle rect = toolItem.getBounds();
					Point point = new Point(rect.x, rect.y + rect.height);
					point = toolBar.toDisplay(point);
					menu.setLocation(point.x, point.y);
					menu.setVisible(true);
				}
			}
		});
		toolItem.addListener(SWT.Selection, new ModeSettingListener(wBoard));

	}
	public void addBoardArea(WBoard wBoard, Canvas canvas){
		
		// mainShell(mainComposite) 
		Composite mainComposite = new Composite(mainShell, SWT.NONE);
		mainComposite.setBounds(MAIN_SHELL_X, MAIN_SHELL_Y + Constants.ICON_SIZE,
				MAIN_SHELL_WIDTH - (Constants.ICON_SIZE + Constants.CLERK_SIZE + Constants.GUI_OFFSET), 
				MAIN_SHELL_HEIGHT - (Constants.ICON_SIZE + Constants.GUI_OFFSET));
		
		 

		Composite canvasComposite = new Composite(mainComposite, SWT.NONE);
		canvasComposite.setBounds(mainComposite.getClientArea());

		/******** The creation of the Canvas *********/
		canvas.setParent(canvasComposite);
		canvas.setBounds(canvasComposite.getClientArea());
		canvas.setBackground(Constants.WHITE);

		// 그리기 이벤트 리스너
		DrawingListener drawingListener = new DrawingListener(wBoard);
		canvas.addListener(SWT.MouseDown, drawingListener);
		canvas.addListener(SWT.MouseMove, drawingListener);
		canvas.addListener(SWT.MouseUp, drawingListener);
		
		// redraw 이벤트 리스너
		canvas.addPaintListener(new CanvasPaintListener(wBoard));

		// 도형 이벤트 리스너
		ShapeListener shapeListener = new ShapeListener(wBoard, SHAPETYPE.ARROW);
		canvas.addListener(SWT.MouseDown, shapeListener);
		canvas.addListener(SWT.MouseMove, shapeListener);
		canvas.addListener(SWT.MouseUp, shapeListener);
		canvas.addListener(SWT.Paint, shapeListener);

		// 객체 선택 이벤트 리스너
		SelectListener selectListener = new SelectListener(wBoard);
		canvas.addListener(SWT.MouseDown, selectListener);
		canvas.addListener(SWT.MouseMove, selectListener);

		// 객체 삭제 이벤트 리스너
		canvas.addListener(SWT.KeyDown, new DeleteListener(wBoard));
		
	}
	
	// 객체를 관리할 수 있는 GUI
	public void addObjectControl(WBoard wBoard ){
		Composite objectComposite = new Composite(mainShell, SWT.TOOL);
		objectComposite.setBounds(MAIN_SHELL_WIDTH - (Constants.CLERK_SIZE + Constants.GUI_OFFSET),
				Constants.GUI_OFFSET,
				Constants.CLERK_SIZE, 
				MAIN_SHELL_HEIGHT);

		Rectangle objectListArea = objectComposite.getClientArea();
		
		// add object list label to objectComposite
		CLabel label = new CLabel(objectComposite, SWT.NONE);
		label.setFont(wBoard.getBoardFont());
		label.setText("Object List");
		label.setBounds(objectListArea.x, objectListArea.y,	Constants.CLERK_SIZE - Constants.GUI_OFFSET - 50,  35);

		// add object list to objectComposite
		itemList = new List(objectComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		itemList.setFont(wBoard.getBoardFont());
		itemList.addListener(SWT.MouseDown, new ListSelectionListener(wBoard));
		itemList.setBounds(objectListArea.x, objectListArea.y  + 70, Constants.CLERK_SIZE - Constants.GUI_OFFSET, (MAIN_SHELL_HEIGHT / 2 - Constants.TABITEM_SIZE) - 50);
		
		// add user list label to objectComposite
		CLabel uListLabel = new CLabel(objectComposite, SWT.NONE);
		uListLabel.setFont(new Font(mainShell.getDisplay(), 
				"consolas", 17, SWT.BOLD));
		uListLabel.setText("User List");
		uListLabel.setBounds(objectListArea.x, Constants.ULISTTITLE_Y,	Constants.CLERK_SIZE - Constants.GUI_OFFSET,  35);
		
		// add user list to objectComposite
		userList = new List(objectComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL);
		userList.setFont(wBoard.getBoardFont());
		userList.addListener(SWT.MouseDown, new ListSelectionListener(wBoard));
		userList.setBounds(objectListArea.x, Constants.USERLIST_Y ,	Constants.CLERK_SIZE - Constants.GUI_OFFSET, Constants.USERLIST_SIZE_Y);

		
		objectComposite.pack();
	}

	public void addListItem(WClientObject wcObject){
		
		StringBuffer sb = new StringBuffer();
		sb.append(wcObject.getOid());
		sb.append(" : ");
		sb.append(wcObject.getObjType());

		itemList.add(sb.toString());
	}

	// 캔버스의 모든 객체를 다시 그림
	public void redraw(WBoard wBoard){
		GC gc = new GC(wBoard.getCanvas());
		
		// redraw all wcObjects
		for(WClientObject wcObject: wBoard.getWObjectList()){
				
			OBJTYPE objectType = wcObject.getObjType();

			gc.setForeground(wcObject.getLineColor());
			gc.setBackground(wcObject.getFaceColor());
			gc.setLineWidth(wcObject.getLineWidth());
			gc.setLineCap(wcObject.getLineCap());
			
			// call appropriate draw method
			if(objectType.equals(MODE.FREE_SHAPE)){
				Drawing drawing = (Drawing) wcObject;
				drawing.draw(gc);
			}
			else if(objectType.equals(MODE.SHAPE)){
				Shape shape = (Shape)wcObject;
				shape.draw(gc);
			}
			else{
				wcObject.draw(gc);
			}
			
		}
		
		
		// draw focus of all items selected in the item list
		gc.setForeground(Constants.BLACK);
		gc.setBackground(Constants.WHITE);
		gc.setLineWidth(Constants.DEFAULT_LINE);
		gc.setLineCap(SWT.CAP_ROUND);
		gc.setFont(wBoard.getBoardFont());
		
		if(itemList != null){
			String[] selectedList = itemList.getSelection();

			for(int i = 0; i < selectedList.length; i++){
				int objectIndex = Integer.parseInt(selectedList[i].substring(0, selectedList[i].indexOf(" :")));

				for(WClientObject wcObject: wBoard.getWObjectList()){
					if(wcObject.getOid() == objectIndex){
						gc.setLineStyle(SWT.LINE_DOT);
						gc.setLineWidth(3);
						
						// 선택된 객체 표시  
						gc.drawFocus(wcObject.getBoundary().x - 1,		
								wcObject.getBoundary().y - 1, 
								wcObject.getBoundary().width,
								wcObject.getBoundary().height);
					}
				}
			}
		}
		gc.dispose();
	}

	public List getItemList() {
		return itemList;
	}

	public Shell getMainShell() {
		return mainShell;
	}
}
