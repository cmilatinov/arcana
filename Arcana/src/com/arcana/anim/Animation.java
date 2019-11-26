package com.arcana.anim;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * Represents an animation to be used when rendering character models or menus.
 */
public class Animation {
	
	/**
	 * The frames that make up the animation.
	 */
	private BufferedImage[] frames;
	
	/**
	 * The index of the current frame.
	 */
	private int timer;
	
	/**
	 * The delay in seconds between each frame of the animation.
	 */
	private double delay;
	
	/**
	 * A timestamp of the last instant when the animation was started.
	 */
	private long lastTime;
	
	/**
	 * Creates an instance of an animation.
	 * @param frames The frames that compose the animation (in order).
	 * @param delay The delay in seconds between two subsequent animation frames.
	 */
	public Animation(BufferedImage[] frames, double delay){
		this.frames = frames;
		this.delay = delay;
	}
	
	/**
	 * Renders the animation using Graphics.
	 * @param g The Graphics object to draw with.
	 * @param r The Rectangle in which the animation is to be rendered on screen.
	 * @return [void]
	 */
	public void render(Graphics g, Rectangle2D r){
		long currentTime = System.nanoTime();
		if(currentTime - lastTime >= 1000000000 / (1 / delay)){
			timer++;
			if(timer >= frames.length)
				timer = 0;
			lastTime = currentTime;
		}
		g.drawImage(frames[timer], (int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight(), null);
	}
	
	/**
	 * Restarts the animation.
	 * @return [void]
	 */
	public void restartAnim(){
		this.timer = 0;
	}
	
	/**
	 * Sets the animation to a specific frame.
	 * @return [void]
	 */
	public void seekToFrame(int frame){
		this.timer = frame;
	}
	
	/**
	 * Returns the index of the current frame in the animation.
	 * @return [int] The frame index.
	 */
	public int currentFrame(){
		return this.timer;
	}
}
