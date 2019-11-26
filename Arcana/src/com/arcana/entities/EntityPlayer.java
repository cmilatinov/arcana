package com.arcana.entities;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import com.arcana.characters.Character;
import com.arcana.src.Arcana;
import com.arcana.utils.Vector2d;

public class EntityPlayer implements Entity{
	
	double width, height;
	double velX = 0, velY = 0;
	double x, y;
	public boolean isFalling = true;
	public boolean isWalking = false;
	Character character;
	
	public EntityPlayer(Character c, double x, double y){
		this.character = c;
		character.loadCharacter();
		this.width = (int) c.getBounds().getWidth();
		this.height = (int) c.getBounds().getHeight();
		this.x = x;
		this.y = y;
	}
	
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}
	
	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public void setVelocityY(double velocity){
		this.velY = velocity;
	}
	
	public void setVelocityX(double velocity){
		this.velX = velocity;
	}
	
	/**
	 * Description of method.
	 * @param velocity Description of param.
	 * @return [int] Description of return.
	 * */
	public void setVelocity(Vector2d velocity){
		this.velX =	velocity.getX();
		this.velY = velocity.getY();
	}
	
	public Vector2d getVelocity(){
		return new Vector2d(velX, velY);
	}
	
	public void setPosition(double x, double y){
		this.x = x;
		this.y = y;
	}
	
	public Point2D getPosition(){
		return new Point2D.Double(x, y);
	}
	
	public void render(Graphics g){
		character.render(g, x, y);
	}
	
	public void crouch(){
		
	}
	
	public void uncrouch(){
		
	}
	
	public boolean affectedByGravity(){
		return true;
	}
	
	public void jump(){
		if(this.isFalling) return;
		this.setVelocityY(character.getJumpVelocity());
		this.isFalling = true;
	}
	
	public void walkLeft(){
		this.setVelocityX(-this.getRunSpeed());
		character.playRunLeftAnim();
	}
	
	public void walkRight(){
		this.setVelocityX(this.getRunSpeed());
		character.playRunRightAnim();
	}
	
	public void stopWalking(){
		this.setVelocityX(0);
		character.stopRunAnim();
	}
	
	public void ability1(String args){
		character.castAbility1(args);
	}
	
	public boolean isFalling(){
		return isFalling;
	}
	
	public boolean isCharacterInAction(){
		return character.isCharacterInAction();
	}
	
	public void despawn(){
		
	}
	
	public Character getCharacter(){
		return character;
	}
	
	public void tick(){
		if(!isFalling)
			this.setVelocityY(0);
		
		this.setPosition(x + velX, y + velY);
		
		if(y < - 1 + (character.getBounds().getHeight() + 1D/540D)){
			y = - 1 + (character.getBounds().getHeight() + 1D/540D);
			isFalling = false;
		}
		if(isFalling){
			this.setVelocityY(velY + Arcana.ACCELERATION);
		}
		
		if(this.velX == 0 || this.velY != 0){
			character.stopRunAnim();
		}
		character.tick(x, y);
	}
	
	public double getRunSpeed(){
		return character.getCharRunSpeed();
	}
}
