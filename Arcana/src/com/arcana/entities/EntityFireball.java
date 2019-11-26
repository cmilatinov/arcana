package com.arcana.entities;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Vector;

import com.arcana.anim.Animation;
import com.arcana.network.Server;
import com.arcana.src.Arcana;
import com.arcana.src.Display;
import com.arcana.utils.Vector2d;

public class EntityFireball implements Entity{
	
	private Animation anim;
	
	private double x, y;
	private boolean spawnedByClient;
	double width = 50D / 1920D, height = 50D / 1080D, jumpVel = 0.045D;
	double velX = 0, velY = 0;
	
	public EntityFireball(double x, double y, double velX, double velY, Animation anim, boolean client){
		this.setPosition(x, y);
		this.setVelocityX(velX);
		this.setVelocityY(velY);
		this.anim = anim;
		this.spawnedByClient = client;
	}
	
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public boolean spawnedByClient(){
		return this.spawnedByClient;
	}
	
	public Point2D getPosition() {
		return new Point2D.Double(x, y);
	}

	public void render(Graphics g) {
		Rectangle2D r = Display.convertRectToScreen(new Rectangle2D.Double(x, y, width, height));
		anim.render(g, r);
	}

	public void tick() {
		x += velX;
		y += velY;
		if(y > 1.0D || y < -1.0D || x > 1.0D || x < -1.0D)
			despawn();
	}
	
	public void despawn(){
		if(Arcana.getNetworkManager() instanceof Server)
			Arcana.getGame().getController().removeEntity(this);
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
	
	public Vector2d getVelocity(){
		return new Vector2d(velX, velY);
	}
	
	public void setVelocityY(double velocity){
		this.velY = velocity;
	}
	
	public void setVelocityX(double velocity){
		this.velX = velocity;
	}
	
	public void setVelocity(Vector<Integer> velocity){
		this.velX = velocity.elementAt(0);
		this.velY = velocity.elementAt(1);
	}
	
	public boolean affectedByGravity(){
		return false;
	}
}
