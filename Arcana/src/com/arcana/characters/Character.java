package com.arcana.characters;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

/**
 * Interface used to represent the structure of a character class. 
 */
public interface Character {
	
	/**
	 * Returns the collision bounds of the character.
	 * @return [Rectangle2D] The rectangle describing the character bounds.
	 */
	public Rectangle2D getBounds();
	
	/**
	 * Returns the crouched collision bounds of the character.
	 * @return [Rectangle2D] The rectangle describing the character bounds.
	 */
	public Rectangle2D getCrouchedBounds();
	
	/**
	 * Loads all necessary resources to render the character.
	 * @return [void]
	 */
	public void loadCharacter();
	
	/**
	 * Returns the character's name.
	 * @return [String] The character name.
	 */
	public String getName();
	
	/**
	 * Renders the character on screen.
	 * @param g The Graphics object used to render the character.
	 * @param x The x-coordinate of the character's position.
	 * @param y The y-coordinate of the character's position.
	 * @return [void]
	 */
	public void render(Graphics g, double x, double y);
	
	/**
	 * Renders a character on screen with a specified animation and frame.
	 * @param anim
	 * @param f
	 * @param g
	 * @param x
	 * @param y
	 */
	public void renderAnim(String anim, int f, Graphics g, double x, double y);
	
	/**
	 * Returns the velocity given to the character upon jumping.
	 * @return []
	 */
	public double getJumpVelocity();
	
	public double getCharRunSpeed();
	
	public void playRunRightAnim();
	
	public void playRunLeftAnim();
	
	public void castAbility1(String args);
	
	public void stopRunAnim();

	public void tick(double x, double y);
	
	public boolean isCharacterInAction();
	
	public String currentAnimationName();

	public int currentAnimationFrame();
	
}
