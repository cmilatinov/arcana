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

import javax.sound.sampled.Mixer;
import javax.swing.JFrame;

import com.arcana.audio.AudioLibrary;
import com.arcana.input.Mouse;
import com.arcana.settings.Settings;
import com.arcana.src.Arcana;

public class AudioDeviceMenu implements SubMenu{
	
	public int currentItem = 1;
	private int transitionItem = -1;
	private boolean isInTransition = false;
	private Rectangle[] itemPositions;
	private Rectangle2D[] actualPositions;
	private String[] items;
	private int timer;
	private int spaceBetweenItems;
	private Font font, titleFont;
	private JFrame frame;
	private AudioMenu menu;
	private Mixer.Info[] audioDevices = AudioLibrary.getSoundDevices();
	private final String title = "Select Audio Device";
	
	private Settings s;
	
	public AudioDeviceMenu(JFrame frame, AudioMenu menu, Settings s){
		this.items = new String[audioDevices.length];
		for(int i = 0; i < audioDevices.length; i++){
			if(audioDevices[i].getName().trim().length() > 35)
				this.items[i] = audioDevices[i].getName().trim().substring(0, 32) + "...";
			else
				this.items[i] = audioDevices[i].getName().trim();
			if(audioDevices[i].getName().trim().equals("Primary Sound Driver"))
				this.items[i] = "Default Sound Device";
		}
		this.menu = menu;
		this.frame = frame;
		this.font = new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 8 / 108);
		this.titleFont =  new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 14 / 108);
		this.spaceBetweenItems = frame.getContentPane().getHeight() * 2 / 108;
		itemPositions = new Rectangle[items.length];
		FontMetrics fm = frame.getGraphics().getFontMetrics(font);
		int startY = (frame.getContentPane().getHeight() * 3 / 7);
		for(int i = 0; i < items.length; i++){
			double strWidth = fm.getStringBounds(items[i], frame.getGraphics()).getWidth();
			double strHeight = fm.getStringBounds(items[i], frame.getGraphics()).getHeight();
			int x = (frame.getContentPane().getWidth() / 2) - (int)(strWidth / 2);
			int y = startY + (int)(strHeight * (i + 1) + (spaceBetweenItems * i)) - (int)strHeight;
			itemPositions[i] = new Rectangle(x, y, (int)strWidth, (int)strHeight);
		}
		this.s = s;
	}
	
	public void render(Graphics g){
			Graphics2D g2d = (Graphics2D) g;
			g.setColor(new Color(0, 50, 255));
			g.setFont(titleFont);
			int strWidth = (int) g.getFontMetrics(titleFont).getStringBounds(title, g).getWidth();
			g.drawString(title, (frame.getContentPane().getWidth() / 2) - (int)(strWidth / 2), 30 + (frame.getContentPane().getHeight() * 14 / 108));
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
	}
	
	public void tick(){
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
	}
	
	public void playAnimationIn(){
		isInTransition = true;
	}
	
	public void playAnimationOut(){
		isInTransition = true;
		timer = Arcana.ticksPerSec / 10;
	}
	
	public void selectItem(int index){
		if(currentItem != index){
			isInTransition = true;
			transitionItem = index;
		}
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
	}
	
	public void mousePressed(MouseEvent e){
	}
	
	private Rectangle2D getStringBounds(String str, Graphics2D g2d, int x, int y){
		FontRenderContext frc = g2d.getFontRenderContext();
		GlyphVector gv = g2d.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}
	
	public void buttonPressed(int index){
		AudioLibrary.setAudioDevice(AudioLibrary.getDeviceByName(audioDevices[index].getName()));
		s.set("Audio", "soundDevice", audioDevices[index].getName());
		menu.state = AudioMenu.AudioMenuState.Audio;
	}
	
	public int getCurrentItem(){
		return currentItem;
	}
}
