package com.arcana.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.arcana.anim.Animation;
import com.arcana.src.Display;
import com.arcana.utils.Vector2d;

public class EntityPlatform implements Entity{

	private Animation anim;
	
	private double x, y;
	double width = 1, height = 0.2, jumpVel = 0.045D;
	double velX = 0, velY = 0;
	
	public EntityPlatform(double x, double y, double velX, double velY, Animation anim){
		this.setPosition(x, y);
		this.setVelocityX(velX);
		this.setVelocityY(velY);
		this.anim = anim;
	}
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public void setX(double x){
		this.x = x;
	}
	
	public void setY(double y){
		this.y = y;
	}
	
	public Point2D getPosition() {
		return new Point2D.Double(x, y);
	}
	
	public Rectangle2D getBounds() {
		return new Rectangle2D.Double(x, y, width, height);
	}

	@Override
	public void render(Graphics g) {
		
		Graphics2D g2D = (Graphics2D) g;
		
		// TODO Auto-generated method stub
		g.setColor(Color.BLACK);
		Rectangle2D rect = Display.convertRectToScreen(getBounds());
		g2D.fill(rect);
		
	}

	@Override
	public void tick() {
		// TODO Auto-generated method stub
		//collision
	}

	@Override
	public boolean affectedByGravity() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setVelocityX(double var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVelocityY(double var) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Vector2d getVelocity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void despawn() {
		// TODO Auto-generated method stub
		
	}

	
	
}
