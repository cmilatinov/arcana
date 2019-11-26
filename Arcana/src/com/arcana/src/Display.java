package com.arcana.src;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

public class Display {
	
	/**	Returns a JFrame created with the specified parameters. 
	 * @param title The title of the JFrame.
	 * @param size The size of the JFrame in a Dimension (new Dimension(sizeX, sizeY)).
	 * @param resizable Whether or not the user should be able to resize the frame.
	 * @param fullscreen Whether or not the frame should be fullscreen.
	 * */
	public static JFrame createFrame(String title, Dimension size, boolean resizable, int fullscreen, Arcana a){
		JFrame frame = new JFrame(title);
		frame.setResizable(resizable);
		if(fullscreen == 2){
			GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
			frame.setUndecorated(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			a.setSize(size);
			frame.add(a);
			frame.pack();
			gd.setFullScreenWindow(frame);
			gd.setDisplayMode(new DisplayMode((int)size.getWidth(), (int)size.getHeight(), 32, gd.getDisplayMode().getRefreshRate()));
			frame.revalidate();
			frame.repaint();
		}else if(fullscreen == 1){
			frame.setUndecorated(true);
			frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
			a.setPreferredSize(Toolkit.getDefaultToolkit().getScreenSize());
			frame.add(a);
			frame.pack();
			frame.revalidate();
			frame.repaint();
		}
		else if(fullscreen == 0){
			a.setPreferredSize(size);
			frame.add(a);
			frame.pack();
			frame.revalidate();
			frame.repaint();
		}
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		//TODO frame.setIconImage(image);
		return frame;
	}
	
	public static Point2D convertPointToScreen(Point2D point){
		Dimension screenSize = new Dimension((int)Arcana.frame.getContentPane().getWidth(), (int)Arcana.frame.getContentPane().getHeight());
		return new Point2D.Double(point.getX() * (screenSize.getWidth() / 2) + (screenSize.getWidth() / 2), (screenSize.getHeight() / 2) - point.getY() * (screenSize.getHeight() / 2));
	}
	
	public static Rectangle2D convertRectToScreen(Rectangle2D rect){
		Dimension screenSize = new Dimension((int)Arcana.frame.getContentPane().getWidth(), (int)Arcana.frame.getContentPane().getHeight());
		Point2D p = convertPointToScreen(new Point2D.Double(rect.getMinX(), rect.getMinY()));
		
		return new Rectangle2D.Double(p.getX() - (rect.getWidth() * screenSize.getWidth() / 2), p.getY() - (rect.getHeight() * screenSize.getHeight() / 2), rect.getWidth() * screenSize.getWidth(), rect.getHeight() * screenSize.getHeight());
	}
}
