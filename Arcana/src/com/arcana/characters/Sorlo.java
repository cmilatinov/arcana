package com.arcana.characters;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.swing.JFrame;

import com.arcana.anim.Animation;
import com.arcana.anim.Animations;
import com.arcana.entities.EntityFireball;
import com.arcana.loaders.ImageLoader;
import com.arcana.network.Client;
import com.arcana.network.Server;
import com.arcana.src.Arcana;
import com.arcana.src.Display;
import com.utils.src.Logger;

public class Sorlo implements Character{
	
	private final String name = "Sorlo";
	double width = 80D / 1920D, height = 137D / 1080D, jumpVel = 0.045D;
	
	private Animation[] animations;
	private boolean isRunning = false;
	private int direction = 0;
	
	private long ability1Timer = 0;
	private float ability1CastTime = 0.75f;
	private float ability1FireballSpawnTime = 0.2f;
	private boolean isCastingAbility1 = false;
	private boolean spawned = false;
	private String nextUUID = "";
	
	private final double fireballVelocity = 0.030D;
	private final double ability1CD = 0.5D;
	
	private BufferedImage sprite;
	
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(0, 0, width, height);
	}
	
	public Rectangle2D getCrouchedBounds() {
		return new Rectangle2D.Double(0, 0, width, 2 * height / 3);
	}

	public void loadCharacter() {
		try {
			sprite = ImageLoader.loadImage(Arcana.getJarPath() + "/res/chars/sorlo/spritesheet.png");
			BufferedImage idleRight1 = ImageLoader.getSprite(sprite, new Rectangle(50, 13, 75, 75));
			BufferedImage idleRight2 = ImageLoader.getSprite(sprite, new Rectangle(126, 13, 75, 75));
			BufferedImage idleRight3 = ImageLoader.getSprite(sprite, new Rectangle(202, 13, 75, 75));
			Animation idleRight = new Animation(new BufferedImage[]{idleRight1, idleRight2, idleRight3}, 0.20f);
			Animations.addAnim("SorloIdleRight", idleRight);
			
			BufferedImage idleLeft1 = ImageLoader.flipHoriz(idleRight1);
			BufferedImage idleLeft2 = ImageLoader.flipHoriz(idleRight2);
			BufferedImage idleLeft3 = ImageLoader.flipHoriz(idleRight3);
			Animation idleLeft = new Animation(new BufferedImage[]{idleLeft1, idleLeft2, idleLeft3}, 0.20f);
			Animations.addAnim("SorloIdleLeft", idleLeft);
			
			BufferedImage runRight1 = ImageLoader.getSprite(sprite, new Rectangle(27, 102, 75, 75));
			BufferedImage runRight2 = ImageLoader.getSprite(sprite, new Rectangle(107, 102, 75, 75));
			BufferedImage runRight3 = ImageLoader.getSprite(sprite, new Rectangle(188, 102, 75, 75));
			BufferedImage runRight4 = ImageLoader.getSprite(sprite, new Rectangle(266, 101, 75, 75));
			Animation runRight = new Animation(new BufferedImage[]{runRight1, runRight2, runRight3, runRight4}, 0.17f);
			Animations.addAnim("SorloRunRight", runRight);
			
			BufferedImage runLeft1 = ImageLoader.flipHoriz(runRight1);
			BufferedImage runLeft2 = ImageLoader.flipHoriz(runRight2);
			BufferedImage runLeft3 = ImageLoader.flipHoriz(runRight3);
			BufferedImage runLeft4 = ImageLoader.flipHoriz(runRight4);
			Animation runLeft = new Animation(new BufferedImage[]{runLeft1, runLeft2, runLeft3, runLeft4}, 0.17f);
			Animations.addAnim("SorloRunLeft", runLeft);
			
			BufferedImage ability1Right1 = ImageLoader.getSprite(sprite, new Rectangle(8, 552, 75, 75));
			BufferedImage ability1Right2 = ImageLoader.getSprite(sprite, new Rectangle(69, 552, 75, 75));
			BufferedImage ability1Right3 = ImageLoader.getSprite(sprite, new Rectangle(140, 552, 75, 75));
			BufferedImage ability1Right4 = ImageLoader.getSprite(sprite, new Rectangle(218, 552, 75, 75));
			BufferedImage ability1Right5 = ImageLoader.getSprite(sprite, new Rectangle(301, 552, 75, 75));
			BufferedImage ability1Right6 = ImageLoader.getSprite(sprite, new Rectangle(371, 552, 75, 75));
			Animation ability1Right = new Animation(new BufferedImage[]{ability1Right1, ability1Right2, ability1Right3, ability1Right4, ability1Right5, ability1Right6}, 1f / 8f);
			Animations.addAnim("SorloAbility1Right", ability1Right);
			
			BufferedImage ability1Left1 = ImageLoader.flipHoriz(ability1Right1);
			BufferedImage ability1Left2 = ImageLoader.flipHoriz(ability1Right2);
			BufferedImage ability1Left3 = ImageLoader.flipHoriz(ability1Right3);
			BufferedImage ability1Left4 = ImageLoader.flipHoriz(ability1Right4);
			BufferedImage ability1Left5 = ImageLoader.flipHoriz(ability1Right5);
			BufferedImage ability1Left6 = ImageLoader.flipHoriz(ability1Right6);
			Animation ability1Left = new Animation(new BufferedImage[]{ability1Left1, ability1Left2, ability1Left3, ability1Left4, ability1Left5, ability1Left6}, 1f / 8f);
			Animations.addAnim("SorloAbility1Left", ability1Left);
			
			animations = new Animation[]{idleRight, idleLeft, runRight, runLeft, ability1Right, ability1Left};
			
			//Abilities
			
			//BufferedImage fireballCastRight1 = ImageLoader.getSprite(sprite, new Rectangle(444, 576, 35, 35));
			BufferedImage fireballCastRight2 = ImageLoader.getSprite(sprite, new Rectangle(478, 576, 35, 35));
			BufferedImage fireballCastRight3 = ImageLoader.getSprite(sprite, new Rectangle(512, 576, 35, 35));
			BufferedImage fireballCastRight4 = ImageLoader.getSprite(sprite, new Rectangle(545, 576, 35, 35));
			
			//BufferedImage fireballCastLeft1 = ImageLoader.getSprite(sprite, new Rectangle(444, 576, 35, 35));
			BufferedImage fireballCastLeft2 = ImageLoader.flipHoriz(fireballCastRight2);
			BufferedImage fireballCastLeft3 = ImageLoader.flipHoriz(fireballCastRight3);
			BufferedImage fireballCastLeft4 = ImageLoader.flipHoriz(fireballCastRight4);
			
			Animation fireballAnimRight = new Animation(new BufferedImage[]{fireballCastRight2, fireballCastRight3, fireballCastRight4}, 0.13f);
			Animations.addAnim("FireballAnimRight", fireballAnimRight);
			
			Animation fireballAnimLeft = new Animation(new BufferedImage[]{fireballCastLeft2, fireballCastLeft3, fireballCastLeft4}, 0.13f);
			Animations.addAnim("FireballAnimLeft", fireballAnimLeft);
			
			
		} catch (IOException e) {
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem: e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	public void playRunRightAnim(){
		if(isRunning && direction == 1) return;
		this.isRunning = true;
		this.direction = 1;
		animations[2].restartAnim();
	}
	
	public void playRunLeftAnim(){
		if(isRunning && direction == 0) return;
		this.isRunning = true;
		this.direction = 0;
		animations[3].restartAnim();
	}
	
	public void castAbility1(String args){
		if(System.nanoTime() < ability1Timer + (ability1CD * 1000000000)) return;
		isCastingAbility1 = true;
		animations[4].restartAnim();
		ability1Timer = System.nanoTime();
		spawned = false;
		nextUUID = args;
	}
	
	public void stopRunAnim(){
		this.isRunning = false;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isCharacterInAction(){
		if(isCastingAbility1)
			return true;
		else 
			return false;
	}
	
	public String currentAnimationName(){
		if(isCastingAbility1)
			return direction == 1 ? "SorloAbility1Right" : "SorloAbility1Left";
		else if(isRunning)
			return direction == 1 ? "SorloRunRight" : "SorloRunLeft";
		else 
			return direction == 1 ? "SorloIdleRight" : "SorloIdleLeft";
	}
	
	public int currentAnimationFrame(){
		if(isCastingAbility1)
			return direction == 1 ? animations[4].currentFrame() : animations[5].currentFrame();
		else if(isRunning)
			return direction == 1 ? animations[2].currentFrame() : animations[3].currentFrame();
		else 
			return direction == 1 ? animations[0].currentFrame() : animations[1].currentFrame();
	}
	
	public void tick(double x, double y){
		long currentTime = System.nanoTime();
		if(isCastingAbility1 && currentTime - ability1Timer >= ability1CastTime * 1000000000){
			isCastingAbility1 = false;
		}if(isCastingAbility1 && currentTime - ability1Timer >= ability1FireballSpawnTime * 1000000000 && !spawned){
			Arcana.getGame().getController().registerEntity(nextUUID, new EntityFireball(direction == 1 ? x + (65D / 1920D) : x - (65D / 1920D), y, direction == 1 ? fireballVelocity : -fireballVelocity, 0,  direction == 1 ? Animations.getAnim("FireballAnimRight") : Animations.getAnim("FireballAnimLeft"), Arcana.getNetworkManager() instanceof Client ? true : false));
			spawned = true;
		}
	}
	
	public void render(Graphics g, double x, double y) {
		if(isCastingAbility1 && direction == 1){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (5D / 1920D), y, 110D / 1920D, height));
			animations[4].render(g, r);
		}else if(isCastingAbility1 && direction == 0){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (5D / 1920D), y, 110D / 1920D, height));
			animations[5].render(g, r);
		}else if(isRunning && direction == 1){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x - (5D / 1920D), y, 110D / 1920D, height));
			animations[2].render(g, r);
		}else if(isRunning && direction == 0){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (10D / 1920D), y, 110D / 1920D, height));
			animations[3].render(g, r);
		}else if(direction == 1){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x - (5D / 1920D), y, 110D / 1920D, height));
			animations[0].render(g, r);
		}else if(direction == 0){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (10D / 1920D), y, 110D / 1920D, height));
			animations[1].render(g, r);
		}
	}
	
	/**
	 * For Client only.
	 * */
	public void renderAnim(String anim, int f, Graphics g, double x, double y){
		if(Arcana.getNetworkManager() instanceof Server) return;
		
		if(anim.equals("SorloAbility1Right")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (5D / 1920D), y, 110D / 1920D, height));
			animations[4].seekToFrame(f);
			animations[4].render(g, r);
		}else if(anim.equals("SorloAbility1Left")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (5D / 1920D), y, 110D / 1920D, height));
			animations[5].seekToFrame(f);
			animations[5].render(g, r);
		}else if(anim.equals("SorloRunRight")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x - (5D / 1920D), y, 110D / 1920D, height));
			animations[2].seekToFrame(f);
			animations[2].render(g, r);
		}else if(anim.equals("SorloRunLeft")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (10D / 1920D), y, 110D / 1920D, height));
			animations[3].seekToFrame(f);
			animations[3].render(g, r);
		}else if(anim.equals("SorloIdleRight")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x - (5D / 1920D), y, 110D / 1920D, height));
			animations[0].seekToFrame(f);
			animations[0].render(g, r);
		}else if(anim.equals("SorloIdleLeft")){
			Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x + (10D / 1920D), y, 110D / 1920D, height));
			animations[1].seekToFrame(f);
			animations[1].render(g, r);
		}
	}

	public double getJumpVelocity() {
		return jumpVel;
	}

	public double getCharRunSpeed() {
		return 0.009D;
	}
	
}
