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

import com.arcana.input.Mouse;
import com.arcana.menus.OptionsMenu.OptionsMenuState;
import com.arcana.settings.Settings;
import com.arcana.settings.VideoSettings;
import com.arcana.src.Arcana;

public class VideoMenu implements SubMenu {

	public int currentItem = 1;
	private int transitionItem = -1;
	private boolean isInTransition = false;
	private Rectangle[] itemPositions;
	private Rectangle2D[] actualPositions;
	private String[] items = { "Resolution", "Window Mode", "Maximum FPS", "Apply Changes", "Back" };
	private int timer;
	private int spaceBetweenItems;
	private Font font, titleFont;
	private JFrame frame;
	private OptionsMenu menu;
	private final String title = "Video Settings";
	Settings s;

	private Rectangle[] buttonArrayLeft = new Rectangle[3], buttonArrayRight = new Rectangle[3];
	private String[] windowModes = { "Windowed", "Borderless", "Fullscreen" };
	public Slider[] sliders = new Slider[3];

	public VideoMenu(JFrame frame, OptionsMenu menu, Settings s) {
		this.menu = menu;
		this.frame = frame;
		this.font = new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 8 / 108);
		this.titleFont =  new Font("Yoster Island", Font.PLAIN, frame.getContentPane().getHeight() * 14 / 108);
		this.spaceBetweenItems = frame.getContentPane().getHeight() * 1 / 108;
		itemPositions = new Rectangle[items.length];
		FontMetrics fm = frame.getGraphics().getFontMetrics(font);
		int startY = (frame.getContentPane().getHeight() / 2);
		for (int i = 0; i < items.length; i++) {
			double strWidth = fm.getStringBounds(items[i], frame.getGraphics()).getWidth();
			double strHeight = fm.getStringBounds(items[i], frame.getGraphics()).getHeight();
			int x = (frame.getContentPane().getWidth() / 4) - (int) (strWidth / 2);
			int y = startY + (int) (strHeight * (i + 1) + (spaceBetweenItems * i)) - (int) strHeight;
			itemPositions[i] = new Rectangle(x, y, (int) strWidth, (int) strHeight);
		}
		this.s = s;
		this.sliders[0] = new Slider(VideoSettings.getResolutions());
		this.sliders[1] = new Slider(windowModes);
		this.sliders[2] = new Slider(VideoSettings.getFrameRates());
		resetSliders();
	}

	private void resetSliders() {
		this.sliders[0].setToHighestValue();
		this.sliders[0].setValue(s.get("Frame", "width") + "x" + s.get("Frame", "height"));
		try {
			this.sliders[1].setIndex(Integer.parseInt(s.get("Frame", "fullscreen")));
		} catch (Exception e) {
			this.sliders[1].setIndex(0);
			s.set("Frame", "fullscreen", "0");
		}
		try {
			if (Integer.parseInt(s.get("Frame", "maxFPS")) == 0)
				this.sliders[2].setValue("Uncapped");
			else {
				this.sliders[2].setIndex(0);
				this.sliders[2].setValue(s.get("Frame", "maxFPS") + " FPS");
			}
		} catch (Exception e) {
			this.sliders[2].setValue("Uncapped");
			s.set("Frame", "maxFPS", "0");
		}
	}

	public void render(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		g.setColor(new Color(0, 50, 255));
		g.setFont(titleFont);
		int strWidth = (int) g.getFontMetrics(titleFont).getStringBounds(title, g).getWidth();
		g.drawString(title, (frame.getContentPane().getWidth() / 2) - (int) (strWidth / 2),
				30 + (frame.getContentPane().getHeight() * 14 / 108));
		g.setFont(font);
		if (this.actualPositions == null) {
			actualPositions = new Rectangle2D[items.length];
			for (int j = 0; j < items.length; j++) {
				actualPositions[j] = getStringBounds(items[j], g2d, (int) itemPositions[j].getMinX(),
						(int) itemPositions[j].getMaxY());
			}
		}

		for (int i = 0; i < items.length; i++) {
			if (Mouse.isMouseIn(actualPositions[i])) {
				g.setColor(Color.CYAN);
			} else {
				g.setColor(Color.white);
			}
			g.drawString(items[i], (int) itemPositions[i].getMinX(), (int) itemPositions[i].getMaxY());
		}
		drawSlider(0, g, VideoSettings.getResolutions(), sliders[0].getCurrentValue());
		drawSlider(1, g, windowModes, sliders[1].getCurrentValue());
		drawSlider(2, g, VideoSettings.getFrameRates(), sliders[2].getCurrentValue());

		g2d.setStroke(new BasicStroke(frame.getContentPane().getHeight() / 216));
		g2d.setStroke(new BasicStroke(frame.getContentPane().getHeight() / 216));
		if (isInTransition) {
			g2d.setColor(Color.white);
			int y = (int) actualPositions[currentItem].getCenterY()
					+ (int) ((actualPositions[transitionItem].getCenterY() - actualPositions[currentItem].getCenterY())
							* timer / (Arcana.ticksPerSec / 10));
			int x = (int) actualPositions[currentItem].getMinX()
					+ (int) ((actualPositions[transitionItem].getMinX() - actualPositions[currentItem].getMinX())
							* timer / (Arcana.ticksPerSec / 10))
					- (frame.getContentPane().getWidth() * 9 / 192);
			int x2 = (int) actualPositions[currentItem].getMaxX()
					+ (int) ((actualPositions[transitionItem].getMaxX() - actualPositions[currentItem].getMaxX())
							* timer / (Arcana.ticksPerSec / 10))
					+ (frame.getContentPane().getWidth() * 9 / 192);
			if (currentItem >= 0 && currentItem <= 2 && transitionItem >= 0 && transitionItem <= 2) {
				x2 = (int) buttonArrayRight[currentItem].getMaxX()
						+ (int) ((buttonArrayRight[transitionItem].getMaxX() - buttonArrayRight[currentItem].getMaxX())
								* timer / (Arcana.ticksPerSec / 10))
						+ (frame.getContentPane().getWidth() * 9 / 192);
			} else if (transitionItem > 2 && currentItem >= 0 && currentItem <= 2) {
				x2 = (int) buttonArrayRight[currentItem].getMaxX()
						+ (int) ((actualPositions[transitionItem].getMaxX() - buttonArrayRight[currentItem].getMaxX())
								* timer / (Arcana.ticksPerSec / 10))
						+ (frame.getContentPane().getWidth() * 9 / 192);
			} else if (currentItem > 2 && transitionItem >= 0 && transitionItem <= 2) {
				x2 = (int) actualPositions[currentItem].getMaxX()
						+ (int) ((buttonArrayRight[transitionItem].getMaxX() - actualPositions[currentItem].getMaxX())
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
			if (currentItem >= 0 && currentItem <= 2) {
				x2 = (int) buttonArrayRight[currentItem].getMaxX() + (frame.getContentPane().getWidth() * 9 / 192);
			}
			g2d.drawLine(0, y, x, y);
			g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192,
					y + (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
			g2d.drawLine(x, y, x + frame.getContentPane().getWidth() * 2 / 192,
					y - (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
			g2d.drawLine(frame.getContentPane().getWidth(), y, x2, y);
			g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192,
					y + (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
			g2d.drawLine(x2, y, x2 - frame.getContentPane().getWidth() * 2 / 192,
					y - (int) ((actualPositions[currentItem].getMaxY() - actualPositions[currentItem].getCenterY())));
		}
	}

	private void drawSlider(int index, Graphics g, String[] values, String currentValue) {
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int leftArrowLength = (int) fm.getStringBounds("<", g).getWidth();
		int rightArrowLength = (int) fm.getStringBounds(">", g).getWidth();
		int widestStringWidth = (int) fm.getStringBounds(getWidestString(values, g), g).getWidth();
		int padding = frame.getContentPane().getWidth() * 4 / 108;
		int middleX = (frame.getContentPane().getWidth() / 2)
				+ ((leftArrowLength + (2 * padding) + widestStringWidth + rightArrowLength) / 2);
		int y = (int) itemPositions[index].getMaxY();
		buttonArrayLeft[index] = (Rectangle) getStringBounds("<", g2d, (frame.getContentPane().getWidth() / 2), y);
		buttonArrayRight[index] = (Rectangle) getStringBounds(">", g2d,
				(frame.getContentPane().getWidth() / 2) + leftArrowLength + (2 * padding) + widestStringWidth, y);
		if (Mouse.isMouseIn(buttonArrayLeft[index]))
			g.setColor(Color.CYAN);
		else
			g.setColor(Color.WHITE);
		if (sliders[index].isDisabled())
			g.setColor(Color.DARK_GRAY);
		g.drawString("<", (frame.getContentPane().getWidth() / 2), y);
		g.setColor(Color.WHITE);
		if (sliders[index].isDisabled())
			g.setColor(Color.DARK_GRAY);
		g.drawString(currentValue, middleX - (int) (fm.getStringBounds(currentValue, g).getWidth() / 2), y);
		if (Mouse.isMouseIn(buttonArrayRight[index]))
			g.setColor(Color.CYAN);
		else
			g.setColor(Color.WHITE);
		if (sliders[index].isDisabled())
			g.setColor(Color.DARK_GRAY);
		g.drawString(">", (frame.getContentPane().getWidth() / 2) + leftArrowLength + (2 * padding) + widestStringWidth,
				y);
	}

	private String getWidestString(String[] stra, Graphics g) {
		int[] lengths = new int[stra.length];
		FontMetrics fm = g.getFontMetrics(g.getFont());
		for (int i = 0; i < stra.length; i++) {
			lengths[i] = (int) fm.getStringBounds(stra[i], g).getWidth();
		}
		int longest = 0;
		for (int i = 0; i < lengths.length; i++) {
			if (lengths[i] > longest)
				longest = lengths[i];
		}
		for (int i = 0; i < lengths.length; i++) {
			if (longest == lengths[i])
				return stra[i];
		}
		return null;
	}

	public void tick() {
		if (isInTransition && timer < Arcana.ticksPerSec / 10) {
			timer++;
		} else if (isInTransition && timer >= Arcana.ticksPerSec / 10) {
			timer = 0;
			isInTransition = false;
			currentItem = transitionItem;
		}
		if (!isInTransition && actualPositions != null) {
			for (int i = 0; i < items.length; i++) {
				if (actualPositions[i] != null && Mouse.isMouseIn(actualPositions[i])) {
					selectItem(i);
				}
			}
		}
		if (buttonArrayLeft[0] != null && buttonArrayLeft[1] != null && buttonArrayLeft[2] != null
				&& buttonArrayRight[0] != null && buttonArrayRight[1] != null && buttonArrayRight[2] != null) {
			for (int i = 0; i < buttonArrayLeft.length; i++) {
				if (Mouse
						.isMouseIn(new Rectangle((int) buttonArrayLeft[i].getMinX(), (int) buttonArrayLeft[i].getMinY(),
								(int) (buttonArrayRight[i].getMaxX() - buttonArrayLeft[i].getMinX()),
								(int) (buttonArrayRight[i].getMaxY() - buttonArrayLeft[i].getMinY())))
						&& !isInTransition) {
					selectItem(i);
				}
			}
		}
		if (sliders[1].getCurrentValue().equals("Fullscreen"))
			sliders[2].disableSlider();
		else
			sliders[2].enableSlider();
		if (sliders[1].getCurrentValue().equals("Borderless"))
			sliders[0].disableSlider();
		else
			sliders[0].enableSlider();
	}

	public void playAnimationIn() {
		isInTransition = true;
	}

	public void playAnimationOut() {
		isInTransition = true;
		timer = Arcana.ticksPerSec / 10;
	}

	public void selectItem(int index) {
		if (currentItem != index) {
			isInTransition = true;
			transitionItem = index;
		}
	}

	public void moveCursorUp() {
		try{
			for (int i = 0; i < items.length; i++) {
				if (i <= 2 && Mouse.isMouseIn(new Rectangle((int) buttonArrayLeft[i].getMinX(), (int) buttonArrayLeft[i].getMinY(),
						(int) (buttonArrayRight[i].getMaxX() - buttonArrayLeft[i].getMinX()),
						(int) (buttonArrayRight[i].getMaxY() - buttonArrayLeft[i].getMinY()))))
					return;
				else if(Mouse.isMouseIn(actualPositions[i]))
					return;
			}
			int moveTo = currentItem - 1;
			if (moveTo < 0)
				moveTo = items.length - 1;
			selectItem(moveTo);
		}catch(NullPointerException e){
			
		}
	}

	public void moveCursorDown() {
		try{
			for (int i = 0; i < items.length; i++) {
				if (i <= 2 && Mouse.isMouseIn(new Rectangle((int) buttonArrayLeft[i].getMinX(), (int) buttonArrayLeft[i].getMinY(),
						(int) (buttonArrayRight[i].getMaxX() - buttonArrayLeft[i].getMinX()),
						(int) (buttonArrayRight[i].getMaxY() - buttonArrayLeft[i].getMinY()))))
					return;
				else if(Mouse.isMouseIn(actualPositions[i]))
					return;
			}
			int moveTo = currentItem + 1;
			if (moveTo > items.length - 1)
				moveTo = 0;
			selectItem(moveTo);
		}catch(NullPointerException e){
			
		}
	}

	public void mouseReleased(MouseEvent e) {
		int y = e.getY();
		int x = e.getX();
		for (int i = 0; i < items.length; i++) {
			Rectangle2D r = actualPositions[i];
			if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
				buttonPressed(i);
				return;
			}
		}
	}

	public void mousePressed(MouseEvent e) {
		int y = e.getY();
		int x = e.getX();
		for (int i = 0; i < buttonArrayLeft.length; i++) {
			Rectangle2D r = buttonArrayLeft[i];
			if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
				if (!sliders[i].isDisabled())
					sliders[i].moveSliderLeft();
				return;
			}
			Rectangle2D r2 = buttonArrayRight[i];
			if (x <= r2.getMaxX() && x >= r2.getMinX() && y <= r2.getMaxY() && y >= r2.getMinY()) {
				if (!sliders[i].isDisabled())
					sliders[i].moveSliderRight();
				return;
			}
		}
	}

	private Rectangle2D getStringBounds(String str, Graphics2D g2d, int x, int y) {
		FontRenderContext frc = g2d.getFontRenderContext();
		GlyphVector gv = g2d.getFont().createGlyphVector(frc, str);
		return gv.getPixelBounds(null, x, y);
	}

	public void buttonPressed(int index) {
		if (index == 0) {
		} else if (index == 1) {
		} else if (index == 2) {
		} else if (index == 3) {
			s.set("Frame", "width", sliders[0].getCurrentValue().split("x")[0]);
			s.set("Frame", "height", sliders[0].getCurrentValue().split("x")[1]);
			s.set("Frame", "fullscreen", sliders[1].getCurrentValue().equals("Fullscreen") ? "2"
					: sliders[1].getCurrentValue().equals("Windowed") ? "0" : "1");
			s.set("Frame", "maxFPS", sliders[2].getCurrentValue().equals("Uncapped") ? "0"
					: sliders[2].getCurrentValue().substring(0, sliders[2].getCurrentValue().length() - 4));
			Arcana.restartGame();
		} else if (index == 4) {
			menu.currentItem = 3;
			menu.setState(OptionsMenuState.Options);
			resetSliders();
		}
	}

	public int getCurrentItem() {
		return currentItem;
	}
}
