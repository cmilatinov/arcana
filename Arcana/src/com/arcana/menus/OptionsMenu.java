package com.arcana.menus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import com.arcana.input.Keyboard;
import com.arcana.input.Mouse;
import com.arcana.menus.Menu.MenuState;
import com.arcana.settings.Settings;
import com.arcana.src.Arcana;

public class OptionsMenu implements SubMenu{
	
	public int currentItem = 1;
	private int transitionItem = -1;
	private boolean isInTransition = false;
	private Rectangle[] itemPositions;
	private Rectangle2D[] actualPositions;
	private String[] items = {"Key Bindings", "Video", "Sound", "Back"};
	private int timer;
	private int spaceBetweenItems;
	private Font font, titleFont;
	private JFrame frame;
	private Menu menu;
	public SubMenu[] submenus = new SubMenu[3];
	public static enum OptionsMenuState{
		Options,
		KeyBindings,
		Video,
		Sound
	}
	public OptionsMenuState optionsState = OptionsMenuState.Options;
	
	Settings s;
	protected Keyboard k;
	
	public OptionsMenu(JFrame frame, Menu menu, Settings s){
		this.menu = menu;
		this.frame = frame;
		this.font = new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 8 / 108);
		this.titleFont =  new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 14 / 108);
		this.spaceBetweenItems = frame.getContentPane().getHeight() * 2 / 108;
		itemPositions = new Rectangle[items.length];
		FontMetrics fm = frame.getGraphics().getFontMetrics(font);
		int startY = (frame.getContentPane().getHeight() / 2) + (((frame.getContentPane().getHeight() / 2) - (((((int)fm.getStringBounds("A", frame.getGraphics()).getHeight()) + spaceBetweenItems) * items.length))) / 2);
		for(int i = 0; i < items.length; i++){
			double strWidth = fm.getStringBounds(items[i], frame.getGraphics()).getWidth();
			double strHeight = fm.getStringBounds(items[i], frame.getGraphics()).getHeight();
			int x = (frame.getContentPane().getWidth() / 4) - (int)(strWidth / 2);
			int y = startY + (int)(strHeight * (i + 1) + (spaceBetweenItems * i)) - (int)strHeight;
			itemPositions[i] = new Rectangle(x, y, (int)strWidth, (int)strHeight);
		}
		this.s = s;
		submenus[0] = new KeyBindingsMenu(frame, this, s);
		submenus[1] = new VideoMenu(frame, this, s);
		submenus[2] = new AudioMenu(frame, this, s);
	}
	
	public void setKeyboard(Keyboard k){
		this.k = k;
	}
	
	public void render(Graphics g){
		if(optionsState == OptionsMenuState.Options){
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(new Color(0, 50, 255));
			g.setFont(titleFont);
			int strWidth = (int) g.getFontMetrics(titleFont).getStringBounds("Options", g).getWidth();
			g.drawString("Options", (frame.getContentPane().getWidth() / 2) - (int)(strWidth / 2), 30 + (frame.getContentPane().getHeight() * 14 / 108));
			g.setFont(font);
			if(this.actualPositions == null){
				actualPositions = new Rectangle2D[items.length];
				for(int j = 0; j < items.length; j++){
					actualPositions[j] = getStringBounds(items[j], g2d, (int)itemPositions[j].getMinX(), (int)itemPositions[j].getMaxY());
				}
			}
			
			for(int i = 0; i < items.length; i++){
				if(Mouse.isMouseIn(actualPositions[i])){
					g.setColor(Color.CYAN);
				}else{
					g.setColor(Color.white);
				}
				g.drawString(items[i], (int)itemPositions[i].getMinX(), (int)itemPositions[i].getMaxY());
			}
			
			g2d.setStroke(new BasicStroke(frame.getContentPane().getHeight() / 216));
			if(isInTransition){
				g2d.setColor(Color.white);
				int y = (int)actualPositions[currentItem].getCenterY() + (int)((actualPositions[transitionItem].getCenterY() - actualPositions[currentItem].getCenterY()) * timer / (Arcana.ticksPerSec / 10));
				int x = (int)actualPositions[currentItem].getMinX() + (int)((actualPositions[transitionItem].getMinX() - actualPositions[currentItem].getMinX()) * timer / (Arcana.ticksPerSec / 10)) - (frame.getContentPane().getWidth() * 9 / 192);
				int x2 = (int)actualPositions[currentItem].getMaxX() + (int)((actualPositions[transitionItem].getMaxX() - actualPositions[currentItem].getMaxX()) * timer / (Arcana.ticksPerSec / 10)) + (frame.getContentPane().getWidth() * 9 / 192);
				g2d.drawLine(0, y, x, y);
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y + (int)((actualPositions[transitionItem].getMaxY() - actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y - (int)((actualPositions[transitionItem].getMaxY() - actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(frame.getContentPane().getWidth(), y, x2, y);
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y + (int)((actualPositions[transitionItem].getMaxY() - actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y - (int)((actualPositions[transitionItem].getMaxY() - actualPositions[transitionItem].getCenterY())));
			}else{
				g2d.setColor(Color.white);
				int x = (int)actualPositions[currentItem].getMinX() - (frame.getContentPane().getWidth() * 9 / 192);
				int x2 = (int)actualPositions[currentItem].getMaxX() + (frame.getContentPane().getWidth() * 9 / 192);
				int y = (int)actualPositions[currentItem].getCenterY();
				g2d.drawLine(0, y, x, y);
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y + (int)((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y - (int)((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(frame.getContentPane().getWidth(), y, x2, y);
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y + (int)((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y - (int)((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
			}
		} else if(optionsState == OptionsMenuState.Sound){
			submenus[2].render(g);
		} else if(optionsState == OptionsMenuState.Video){
			submenus[1].render(g);
		} else if(optionsState == OptionsMenuState.KeyBindings){
			submenus[0].render(g);
		}
	}
	
	public void tick(){
		if(optionsState == OptionsMenuState.Options){
			if(isInTransition && timer < Arcana.ticksPerSec / 10){
				timer++;
			}else if(isInTransition && timer >= Arcana.ticksPerSec / 10){
				timer = 0;
				isInTransition = false;
				currentItem = transitionItem;
			}
			if(!isInTransition && actualPositions != null){
				for(int i = 0; i < items.length; i++){
					if(actualPositions[i] != null && Mouse.isMouseIn(actualPositions[i])){
						selectItem(i);
					}
				}
			}
		} else if(optionsState == OptionsMenuState.Sound){
			submenus[2].tick();
		} else if(optionsState == OptionsMenuState.Video){
			submenus[1].tick();
		} else if(optionsState == OptionsMenuState.KeyBindings){
			submenus[0].tick();
		}
	}

	public void selectItem(int index){
		if(currentItem != index){
			isInTransition = true;
			transitionItem = index;
		}
	}
	
	public void setState(OptionsMenuState state){
		this.optionsState = state;
	}
	
	public void moveCursorUp(){
		for(int i = 0; i < items.length; i++){
			if(Mouse.isMouseIn(actualPositions[i]))
				return;
		}
		int moveTo = currentItem - 1;
		if(moveTo < 0)
			moveTo = items.length - 1;
		selectItem(moveTo);
	}
	
	public void moveCursorDown(){
		for(int i = 0; i < items.length; i++){
			if(Mouse.isMouseIn(actualPositions[i]))
				return;
		}
		int moveTo = currentItem + 1;
		if(moveTo > items.length - 1)
			moveTo = 0;
		selectItem(moveTo);
	}
	
	public void mouseReleased(MouseEvent e){
		if(this.optionsState == OptionsMenuState.Options){
			int y = e.getY();
			int x = e.getX();
			for(int i = 0; i < items.length; i++){
				Rectangle2D r = actualPositions[i];
				if(x <= r.getMaxX() && x >= r.getMinX() 
					&& y <= r.getMaxY() && y >= r.getMinY()){
					buttonPressed(i);
					return;
				}
			}
		}else if(this.optionsState == OptionsMenuState.Sound){
			((AudioMenu)submenus[2]).mouseReleased(e);
		}else if(this.optionsState == OptionsMenuState.Video){
			((VideoMenu)submenus[1]).mouseReleased(e);
		} else if(optionsState == OptionsMenuState.KeyBindings){
			((KeyBindingsMenu)submenus[0]).mouseReleased(e);
		}
	}
	
	public void mousePressed(MouseEvent e){
		if(this.optionsState == OptionsMenuState.Options){
			
		}else if(this.optionsState == OptionsMenuState.Sound){
			((AudioMenu)submenus[2]).mousePressed(e);
		}else if(this.optionsState == OptionsMenuState.Video){
			((VideoMenu)submenus[1]).mousePressed(e);
		}else if(optionsState == OptionsMenuState.KeyBindings){
			((KeyBindingsMenu)submenus[0]).mousePressed(e);
		}
	}
	
	private Rectangle2D getStringBounds(String str, Graphics2D g2d, int x, int y){
		FontRenderContext frc = g2d.getFontRenderContext();
		GlyphVector gv = g2d.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	
	public void buttonPressed(int index){
		if(index == 0){
			this.setState(OptionsMenuState.KeyBindings);
		}else if(index == 1){
			((VideoMenu)submenus[1]).currentItem = 1;
			this.setState(OptionsMenuState.Video);
		}else if(index == 2){	
			((AudioMenu)submenus[2]).currentItem = 2;
			this.setState(OptionsMenuState.Sound);
		}else if(index == 3){
			menu.currentItem = 3;
			menu.setState(MenuState.MainMenu);
		}
	}
	
	public int getCurrentItem(){
		return currentItem;
	}
}
