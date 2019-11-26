package com.arcana.entities;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import com.arcana.utils.Vector2d;

public interface Entity {
	
	public void setPosition(double x, double y);
	
	public void setX(double x);
	
	public void setY(double y);
	
	public Point2D getPosition();
	
	public Rectangle2D getBounds();
	
	public void render(Graphics g);
	
	public void tick();
	
	public boolean affectedByGravity();

	public void setVelocityX(double var);

	public void setVelocityY(double var);
	
	public Vector2d getVelocity();
	
	public void despawn();
}
