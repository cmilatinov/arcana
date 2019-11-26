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

import com.arcana.audio.AudioLibrary;
import com.arcana.input.Mouse;
import com.arcana.menus.OptionsMenu.OptionsMenuState;
import com.arcana.settings.Settings;
import com.arcana.src.Arcana;

public class AudioMenu implements SubMenu {

	public int currentItem = 2;
	private int transitionItem = -1;
	private boolean isInTransition = false;
	private Rectangle[] itemPositions;
	private Rectangle2D[] actualPositions;
	private String[] items = { "Master Volume", "SFX Volume", "Music Volume", "Audio Device", "Back" };
	private final String title = "Sound Options";
	private int timer;
	private int spaceBetweenItems;
	private Font font, titleFont;
	private JFrame frame;
	private OptionsMenu menu;

	public AudioDeviceMenu adMenu;

	public AudioMenuState state = AudioMenuState.Audio;

	public static enum AudioMenuState {
		Audio, AudioDevice
	};

	private int[] volumes = new int[3];
	private Rectangle[] buttonArrayLeft = new Rectangle[3], buttonArrayRight = new Rectangle[3];
	private int volChanging = -1;
	private int leftRight = 0;
	private long lastVolTime = System.currentTimeMillis();
	private boolean volChangingWithKeyboard = false;

	private Settings s;

	public AudioMenu(JFrame frame, OptionsMenu menu, Settings s) {
		this.adMenu = new AudioDeviceMenu(frame, this, s);
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
		volumes[0] = (int) (AudioLibrary.getMasterVolume() * 100);
		volumes[1] = (int) (Float.parseFloat(s.get("Audio", "sfxVolume")) * 100);
		volumes[2] = (int) (Float.parseFloat(s.get("Audio", "musicVolume")) * 100);
	}

	public void render(Graphics g) {
		if (state == AudioMenuState.Audio) {
			volumes[0] = (int) (AudioLibrary.getMasterVolume() * 100);
			volumes[1] = (int) (Float.parseFloat(s.get("Audio", "sfxVolume")) * 100);
			volumes[2] = (int) (Float.parseFloat(s.get("Audio", "musicVolume")) * 100);
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

			String[] array = createStringArray();
			for (int i = 0; i < 3; i++) {
				drawSlider(i, g, array, Integer.toString(volumes[i]));
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
				if (currentItem >= 0 && currentItem <= 2 && transitionItem >= 0 && transitionItem <= 2) {
					x2 = (int) buttonArrayRight[currentItem].getMaxX()
							+ (int) ((buttonArrayRight[transitionItem].getMaxX()
									- buttonArrayRight[currentItem].getMaxX()) * timer / (Arcana.ticksPerSec / 10))
							+ (frame.getContentPane().getWidth() * 9 / 192);
				} else if (transitionItem > 2 && currentItem >= 0 && currentItem <= 2) {
					x2 = (int) buttonArrayRight[currentItem].getMaxX()
							+ (int) ((actualPositions[transitionItem].getMaxX()
									- buttonArrayRight[currentItem].getMaxX()) * timer / (Arcana.ticksPerSec / 10))
							+ (frame.getContentPane().getWidth() * 9 / 192);
				} else if (currentItem > 2 && transitionItem >= 0 && transitionItem <= 2) {
					x2 = (int) actualPositions[currentItem].getMaxX()
							+ (int) ((buttonArrayRight[transitionItem].getMaxX()
									- actualPositions[currentItem].getMaxX()) * timer / (Arcana.ticksPerSec / 10))
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
		} else if (state == AudioMenuState.AudioDevice) {
			adMenu.render(g);
		}
	}

	private void drawSlider(int index, Graphics g, String[] values, String currentValue) {
		Graphics2D g2d = (Graphics2D) g;
		FontMetrics fm = g.getFontMetrics(g.getFont());
		int leftArrowLength = (int) fm.getStringBounds("<", g).getWidth();
		int rightArrowLength = (int) fm.getStringBounds(">", g).getWidth();
		int widestStringWidth = (int) fm.getStringBounds(getWidestString(values, g), g).getWidth();
		int padding = frame.getContentPane().getWidth() * 1 / 108;
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
		g.drawString("<", (frame.getContentPane().getWidth() / 2), y);
		g.setColor(Color.WHITE);
		g.drawString(currentValue, middleX - (int) (fm.getStringBounds(currentValue, g).getWidth() / 2), y);
		if (Mouse.isMouseIn(buttonArrayRight[index]))
			g.setColor(Color.CYAN);
		else
			g.setColor(Color.WHITE);
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

	private String[] createStringArray() {
		return new String[] { "100" };
	}

	public void tick() {
		if (state == AudioMenuState.Audio) {
			if (isInTransition && timer < Arcana.ticksPerSec / 10) {
				timer++;
			} else if (isInTransition && timer >= Arcana.ticksPerSec / 10) {
				timer = 0;
				isInTransition = false;
				currentItem = transitionItem;
			}
			if (!isInTransition && actualPositions != null) {
				for (int i = 0; i < items.length; i++) {
					if(actualPositions[i] != null && Mouse.isMouseIn(actualPositions[i])){
						selectItem(i);
					}
				}
			}
			if (buttonArrayLeft[0] != null && buttonArrayLeft[1] != null && buttonArrayLeft[2] != null
					&& buttonArrayRight[0] != null && buttonArrayRight[1] != null && buttonArrayRight[2] != null) {
				if (volChanging != -1 && (Mouse.isMouseDown() || volChangingWithKeyboard)) {
					long currentVolTime = System.currentTimeMillis();
					if (currentVolTime - lastVolTime >= 50) {
						if (leftRight == 0) {
							if (Mouse.isMouseIn(buttonArrayLeft[volChanging]) || volChangingWithKeyboard) {
								if (volChanging == 0) {
									AudioLibrary.setMasterVolume(AudioLibrary.getMasterVolume() - 0.01f < 0.0f ? 0.0f
											: AudioLibrary.getMasterVolume() - 0.01f);
								} else if (volChanging == 1) {
									s.set("Audio", "sfxVolume", Float.toString(
											Float.parseFloat(s.get("Audio", "sfxVolume")) - 0.01f < 0.0f ? 0.0f
													: Float.parseFloat(s.get("Audio", "sfxVolume")) - 0.01f));
									//s.getSounds().setSfxVolume(Float.parseFloat(s.get("Audio", "sfxVolume")));
								} else if (volChanging == 2) {
									s.set("Audio", "musicVolume", Float.toString(
											Float.parseFloat(s.get("Audio", "musicVolume")) - 0.01f < 0.0f ? 0.0f
													: Float.parseFloat(s.get("Audio", "musicVolume")) - 0.01f));
									//s.getSounds().setMusicVolume(Float.parseFloat(s.get("Audio", "musicVolume")));
								}
								lastVolTime = System.currentTimeMillis();
							} else {
								volChanging = -1;
							}
						} else {
							if (Mouse.isMouseIn(buttonArrayRight[volChanging]) || volChangingWithKeyboard) {
								if (volChanging == 0) {
									AudioLibrary.setMasterVolume(AudioLibrary.getMasterVolume() + 0.01f > 1.0f ? 1.0f
											: AudioLibrary.getMasterVolume() + 0.01f);
								} else if (volChanging == 1) {
									s.set("Audio", "sfxVolume", Float.toString(
											Float.parseFloat(s.get("Audio", "sfxVolume")) + 0.01f > 1.0f ? 1.0f
													: Float.parseFloat(s.get("Audio", "sfxVolume")) + 0.01f));
									//s.getSounds().setSfxVolume(Float.parseFloat(s.get("Audio", "sfxVolume")));
								} else if (volChanging == 2) {
									s.set("Audio", "musicVolume", Float.toString(
											Float.parseFloat(s.get("Audio", "musicVolume")) + 0.01f > 1.0f ? 1.0f
													: Float.parseFloat(s.get("Audio", "musicVolume")) + 0.01f));
									//s.getSounds().setMusicVolume(Float.parseFloat(s.get("Audio", "musicVolume")));
								}
								lastVolTime = System.currentTimeMillis();
							} else {
								volChanging = -1;
							}
						}
					}
				} else if (buttonArrayLeft[0] != null && buttonArrayLeft[1] != null && buttonArrayLeft[2] != null
						&& buttonArrayRight[0] != null && buttonArrayRight[1] != null && buttonArrayRight[2] != null) {
					for (int i = 0; i < buttonArrayLeft.length; i++) {
						if (Mouse
								.isMouseIn(new Rectangle((int) buttonArrayLeft[i].getMinX(),
										(int) buttonArrayLeft[i].getMinY(),
										(int) (buttonArrayRight[i].getMaxX() - buttonArrayLeft[i].getMinX()),
										(int) (buttonArrayRight[i].getMaxY() - buttonArrayLeft[i].getMinY())))
								&& !isInTransition) {
							selectItem(i);
						}
					}
				}
			}
		} else if (state == AudioMenuState.AudioDevice) {
			adMenu.tick();
		}
	}

	public void selectItem(int index) {
		if (currentItem != index) {
			isInTransition = true;
			transitionItem = index;
		}
	}

	public void changeVolumeWithKeyboard(int index, int leftRight) {
		volChanging = index;
		this.leftRight = leftRight;
		volChangingWithKeyboard = true;
	}

	public void stopVolumeChange() {
		volChanging = -1;
		this.leftRight = 0;
		volChangingWithKeyboard = false;
	}

	public void moveCursorUp() {
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
	}

	public void moveCursorDown() {
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
	}

	public void mouseReleased(MouseEvent e) {
		if (state == AudioMenuState.Audio) {
			int y = e.getY();
			int x = e.getX();
			for (int i = 0; i < items.length; i++) {
				Rectangle2D r = actualPositions[i];
				if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
					buttonPressed(i);
					return;
				}
			}
		} else if (state == AudioMenuState.AudioDevice) {
			adMenu.mouseReleased(e);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (state == AudioMenuState.Audio) {
			int y = e.getY();
			int x = e.getX();
			for (int i = 0; i < buttonArrayLeft.length; i++) {
				Rectangle r = buttonArrayLeft[i];
				if (x <= r.getMaxX() && x >= r.getMinX() && y <= r.getMaxY() && y >= r.getMinY()) {
					volChanging = i;
					leftRight = 0;
					return;
				}
				Rectangle r2 = buttonArrayRight[i];
				if (x <= r2.getMaxX() && x >= r2.getMinX() && y <= r2.getMaxY() && y >= r2.getMinY()) {
					volChanging = i;
					leftRight = 1;
					return;
				}
			}
		} else if (state == AudioMenuState.AudioDevice) {
			adMenu.mousePressed(e);
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
			state = AudioMenuState.AudioDevice;
		} else if (index == 4) {
			menu.currentItem = 3;
			menu.setState(OptionsMenuState.Options);
		}
	}

	public int getCurrentItem() {
		return currentItem;
	}
}
