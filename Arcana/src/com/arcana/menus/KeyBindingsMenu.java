package com.arcana.menus;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import com.arcana.input.Mouse;
import com.arcana.settings.Settings;
import com.arcana.src.Arcana;

public class KeyBindingsMenu implements SubMenu {

	public int currentItem = 0;
	private int transitionItem = -1;
	private String[] items;
	private String[] keys;
	private Rectangle[] itemPositions;
	private Rectangle2D[] actualPositions;
	private Rectangle2D[] keyPositions;
	private JFrame frame;
	private Font font, titleFont;
	private boolean isInTransition = false;
	private int spaceBetweenItems;
	private int timer;
	private OptionsMenu options;
	private String title = "Key Bindings";
	Settings s;
	

	/**
	 * Initializes the variables needed for the ticking and rendering of the
	 * menu.
	 * 
	 * @param frame
	 *            The JFrame in which the menu is to be rendered.
	 */
	public KeyBindingsMenu(JFrame frame, OptionsMenu options, Settings s) {
		this.frame = frame;
		this.options = options;
		String[] kb = s.getKeyBindings().getAllKeybindings();
		this.items = new String[kb.length + 1];
		for (int i = 0; i < kb.length; i++) {
			this.items[i] = kb[i];
		}
		this.items[kb.length] = "Back";
		this.keys = s.getKeyBindings().getAllKeyStrokes();
		this.spaceBetweenItems = frame.getContentPane().getHeight() * 1 / 216;
		this.font = new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 55 / 1080);
		this.titleFont = new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 14 / 108);
		itemPositions = new Rectangle[items.length];
		FontMetrics fm = frame.getGraphics().getFontMetrics(font);
		int startY = (frame.getContentPane().getHeight() * 18 / 108) + spaceBetweenItems;
		for (int i = 0; i < items.length; i++) {
			double strWidth = fm.getStringBounds(items[i], frame.getGraphics()).getWidth();
			double strHeight = fm.getStringBounds(items[i], frame.getGraphics()).getHeight();
			int x = (frame.getContentPane().getWidth() / 3) - (int) (strWidth / 2);
			if (i == kb.length)
				x = (frame.getContentPane().getWidth() / 2) - (int) (strWidth / 2);
			int y = startY + (int) (strHeight * (i + 1) + (spaceBetweenItems * i)) - (int) strHeight;
			itemPositions[i] = new Rectangle(x, y, (int) strWidth, (int) strHeight);
		}
		this.s = s;
	}

	public void tick() {
		if (isInTransition && timer < Arcana.ticksPerSec / 10) {
			timer++;
		} else if (isInTransition && timer >= Arcana.ticksPerSec / 10) {
			timer = 0;
			isInTransition = false;
			currentItem = transitionItem;
		}
		try {
			if (!isInTransition && actualPositions != null && keyPositions != null) {
				for (int i = 0; i < items.length - 1; i++) {
					if (actualPositions[i] != null && keyPositions[i] != null
							&& Mouse.isMouseIn(new Rectangle((int) actualPositions[i].getMinX(),
									(int) actualPositions[i].getMinY(),
									(int) (keyPositions[i].getMaxX() - actualPositions[i].getMinX()),
									(int) (keyPositions[i].getMaxY() - actualPositions[i].getMinY())))) {
						selectItem(i);
					}
				}
				if (actualPositions[items.length - 1] != null && Mouse.isMouseIn(actualPositions[items.length - 1])) {
					selectItem(items.length - 1);
				}
			}
		} catch (NullPointerException e) {

		}
	}

	public void selectItem(int index) {
		if (currentItem != index) {
			isInTransition = true;
			transitionItem = index;
		}
	}

	public void moveCursorUp() {
		for (int i = 0; i < items.length; i++) {
			if ( i < items.length - 1 && actualPositions[i] != null && keyPositions[i] != null
					&& Mouse.isMouseIn(new Rectangle((int) actualPositions[i].getMinX(),
							(int) actualPositions[i].getMinY(),
							(int) (keyPositions[i].getMaxX() - actualPositions[i].getMinX()),
							(int) (keyPositions[i].getMaxY() - actualPositions[i].getMinY()))))
				return;
			else if(actualPositions[i] != null && Mouse.isMouseIn(actualPositions[i]))
				return;
		}
		int moveTo = currentItem - 1;
		if (moveTo < 0)
			moveTo = items.length - 1;
		selectItem(moveTo);
	}

	public void moveCursorDown() {
		for (int i = 0; i < items.length; i++) {
			if ( i < items.length - 1 && actualPositions[i] != null && keyPositions[i] != null
					&& Mouse.isMouseIn(new Rectangle((int) actualPositions[i].getMinX(),
							(int) actualPositions[i].getMinY(),
							(int) (keyPositions[i].getMaxX() - actualPositions[i].getMinX()),
							(int) (keyPositions[i].getMaxY() - actualPositions[i].getMinY()))))
				return;
			else if(actualPositions[i] != null && Mouse.isMouseIn(actualPositions[i]))
				return;
		}
		int moveTo = currentItem + 1;
		if (moveTo > items.length - 1)
			moveTo = 0;
		selectItem(moveTo);
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(new Color(0, 50, 255));
		g.setFont(titleFont);
		int strWidth = (int) g.getFontMetrics(titleFont).getStringBounds(title, g).getWidth();
		g.drawString(title, (frame.getContentPane().getWidth() / 2) - (int) (strWidth / 2),
				(frame.getContentPane().getHeight() * 17 / 108));
		g.setFont(font);
		if (this.actualPositions == null) {
			actualPositions = new Rectangle2D[items.length];
			for (int j = 0; j < items.length; j++) {
				actualPositions[j] = getStringBounds(items[j], g2d, (int) itemPositions[j].getMinX(),
						(int) itemPositions[j].getMaxY());
			}

		}
		if (this.keyPositions == null) {
			keyPositions = new Rectangle2D[keys.length];
			for (int j = 0; j < keys.length; j++) {
				keyPositions[j] = getStringBounds(keys[j], g2d,
						(frame.getContentPane().getWidth() * 2 / 3)
								- ((int) (g.getFontMetrics(g.getFont()).getStringBounds(keys[j], g).getWidth() / 2)),
						(int) itemPositions[j].getMaxY());
			}
		}
		try {
			for (int i = 0; i < items.length; i++) {
				if (Mouse.isMouseIn(actualPositions[i])) {
					g.setColor(Color.CYAN);
				} else {
					g.setColor(Color.white);
				}
				g.drawString(items[i], (int) itemPositions[i].getMinX(), (int) itemPositions[i].getMaxY());
			}
			for (int i = 0; i < keys.length; i++) {
				if (Mouse.isMouseIn(keyPositions[i])) {
					g.setColor(Color.CYAN);
				} else {
					g.setColor(Color.white);
				}
				g.drawString(keys[i],
						(frame.getContentPane().getWidth() * 2 / 3)
								- ((int) (g.getFontMetrics(g.getFont()).getStringBounds(keys[i], g).getWidth() / 2)),
						(int) itemPositions[i].getMaxY());
			}

			g2d.setStroke(new BasicStroke(frame.getContentPane().getHeight() / 216));
			if (isInTransition) {
				g2d.setColor(Color.white);
				int y = (int) actualPositions[currentItem].getCenterY()
						+ (int) ((actualPositions[transitionItem].getCenterY()
								- actualPositions[currentItem].getCenterY()) * timer / (Arcana.ticksPerSec / 10));
				int x = (int) actualPositions[currentItem].getMinX()
						+ (int) ((actualPositions[transitionItem].getMinX() - actualPositions[currentItem].getMinX())
								* timer / (Arcana.ticksPerSec / 10))
						- (frame.getContentPane().getWidth() * 9 / 192);
				int x2 = (int) actualPositions[currentItem].getMaxX()
						+ (int) ((actualPositions[transitionItem].getMaxX() - actualPositions[currentItem].getMaxX())
								* timer / (Arcana.ticksPerSec / 10))
						+ (frame.getContentPane().getWidth() * 9 / 192);
				if (currentItem < items.length - 1 && transitionItem < items.length - 1) {
					x2 = (int) keyPositions[currentItem].getMaxX()
							+ (int) ((keyPositions[transitionItem].getMaxX() - keyPositions[currentItem].getMaxX())
									* timer / (Arcana.ticksPerSec / 10))
							+ (frame.getContentPane().getWidth() * 9 / 192);
				} else if (currentItem < items.length - 1 && transitionItem == items.length - 1) {
					x2 = (int) keyPositions[currentItem].getMaxX()
							+ (int) ((actualPositions[transitionItem].getMaxX() - keyPositions[currentItem].getMaxX())
									* timer / (Arcana.ticksPerSec / 10))
							+ (frame.getContentPane().getWidth() * 9 / 192);
				} else if (currentItem == items.length - 1 && transitionItem < items.length - 1) {
					x2 = (int) actualPositions[currentItem].getMaxX()
							+ (int) ((keyPositions[transitionItem].getMaxX() - actualPositions[currentItem].getMaxX())
									* timer / (Arcana.ticksPerSec / 10))
							+ (frame.getContentPane().getWidth() * 9 / 192);
				}
				g2d.drawLine(0, y, x, y);
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192,
						y + (int) ((actualPositions[transitionItem].getMaxY()
								- actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192,
						y - (int) ((actualPositions[transitionItem].getMaxY()
								- actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(frame.getContentPane().getWidth(), y, x2, y);
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192,
						y + (int) ((actualPositions[transitionItem].getMaxY()
								- actualPositions[transitionItem].getCenterY())));
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192,
						y - (int) ((actualPositions[transitionItem].getMaxY()
								- actualPositions[transitionItem].getCenterY())));
			} else {
				g2d.setColor(Color.white);
				int x = (int) actualPositions[currentItem].getMinX() - (frame.getContentPane().getWidth() * 9 / 192);
				int x2 = (int) actualPositions[currentItem].getMaxX() + (frame.getContentPane().getWidth() * 9 / 192);
				int y = (int) actualPositions[currentItem].getCenterY();
				if (currentItem < items.length - 1) {
					x2 = (int) keyPositions[currentItem].getMaxX() + (frame.getContentPane().getWidth() * 9 / 192);
				}
				g2d.drawLine(0, y, x, y);
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y
						+ (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192, y
						- (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(frame.getContentPane().getWidth(), y, x2, y);
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y
						+ (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
				g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192, y
						- (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
			}
		} catch (NullPointerException e) {

		}
	}

	/**
	 * Called when the mouse is released.
	 * 
	 * @param e
	 *            The MouseEvent containing the coordinates of the mouse click.
	 */
	public void mouseReleased(MouseEvent e) {
		try{
			int y = e.getY();
			int x = e.getX();
			for (int i = 0; i < items.length; i++) {
				Rectangle2D r = actualPositions[i];
				if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
					buttonPressed(i);
					return;
				}
			}
			for (int i = 0; i < keys.length; i++) {
				Rectangle2D r = keyPositions[i];
				if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
					buttonPressed(i);
					return;
				}
			}
		}catch(NullPointerException ex){
			
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	private Rectangle2D getStringBounds(String str, Graphics2D g2d, int x, int y) {
		FontRenderContext frc = g2d.getFontRenderContext();
		GlyphVector gv = g2d.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}

	/**
	 * Called if menu button is pressed.
	 * 
	 * @param index
	 *            The index of the button in the array.
	 */
	public void buttonPressed(int index) {
		if (index == items.length - 1){
			keys = s.getKeyBindings().getAllKeyStrokes();
			keyPositions = null;
			options.setState(OptionsMenu.OptionsMenuState.Options);
		}else {
			keys = s.getKeyBindings().getAllKeyStrokes();
			keys[index] = "...";
			keyPositions = null;
			options.k.listenForNextKeyStroke();
		}
	}
	
	public void nextKeyStroke(KeyEvent e){
		int keycode = e.getKeyCode();
		if(keycode != 27){
			int index = 0;
			for(int i = 0; i < keys.length; i++){
				if(keys[i].equals("...")){
					index = i;
					break;
				}
			}
			s.getKeyBindings().changeKeyBinding(getKeyBindName(items[index]), keycode);
		}
		keys = s.getKeyBindings().getAllKeyStrokes();
		keyPositions = null;
	}
	
	public String getKeyBindName(String str){
		Character first = str.charAt(0);
		String res = Character.toLowerCase(first) + str.replace(" ", "").substring(1);
		return res;
	}

	public int getCurrentItem() {
		return currentItem;
	}

}
