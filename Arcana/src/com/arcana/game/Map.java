package com.arcana.game;

import java.awt.Graphics;
import java.awt.geom.Point2D;

import com.arcana.entities.Controller;

public interface Map {
	
	public Runnable loadMap(Game g, Controller c);
	
	public void render(Graphics g);
	
	public void tick();
	
	public String getMapName();
	
	public Point2D[] getSpawnLocations();
	
	public boolean mapLoaded();
}
