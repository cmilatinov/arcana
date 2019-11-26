package com.arcana.input;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import com.arcana.menus.Menu;
import com.arcana.src.Arcana;

public class Mouse implements MouseListener, MouseWheelListener{
	
	private Menu m;
	private static boolean mouseDown = false;
	
	public Mouse(Menu m){
		this.m = m;
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		mouseDown = true;
		m.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		mouseDown = false;
		m.mouseReleased(arg0);
	}
	
	public static boolean isMouseDown(){
		return mouseDown;
	}
	
	public static boolean isMouseIn(Rectangle r){
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoc, Arcana.frame.getContentPane());
		int x = (int) mouseLoc.getX();
		int y = (int) mouseLoc.getY();
		if(x <= r.getMaxX() && x >= r.getMinX() 
				&& y <= r.getMaxY() && y >= r.getMinY())
			return true;
		return false;
	}
	
	public static boolean isMouseIn(Rectangle2D r){
		Point mouseLoc = MouseInfo.getPointerInfo().getLocation();
		SwingUtilities.convertPointFromScreen(mouseLoc, Arcana.frame.getContentPane());
		int x = (int) mouseLoc.getX();
		int y = (int) mouseLoc.getY();
		if(x <= r.getMaxX() && x >= r.getMinX() 
				&& y <= r.getMaxY() && y >= r.getMinY())
			return true;
		return false;
	}
	
}
