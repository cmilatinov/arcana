package com.arcana.game;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import com.arcana.anim.Animation;
import com.arcana.entities.Controller;
import com.arcana.loaders.ImageLoader;
import com.arcana.src.Arcana;
import com.utils.src.Logger;

public class TestMap implements Map{
	
	private boolean mapLoaded = false;
	
	private Animation backgroundAnim;
	
	public Runnable loadMap(Game g, Controller c) {
		return new Runnable(){
			public void run() {
				long startTime = System.nanoTime();
				g.loadControls();
				
				try{
					BufferedImage img1 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_1.png");
					BufferedImage img2 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_2.png");
					BufferedImage img3 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_3.png");
					BufferedImage img4 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_4.png");
					BufferedImage img5 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_5.png");
					BufferedImage img6 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_6.png");
					BufferedImage img7 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_7.png");
					BufferedImage img8 = ImageLoader.loadImage(Arcana.getJarPath() + "/res/maps/riverside_cliffs/frame_8.png");
					backgroundAnim = new Animation(new BufferedImage[]{img1, img2, img3, img4, img5, img6, img7, img8}, 1.2D / 8D);
				}catch(IOException e){
					Arcana.LOGGER.println(e.getMessage(), Logger.ERROR);
					for(StackTraceElement elem: e.getStackTrace()){
						Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
					}
				}
				
				long endTime = System.nanoTime();
				Arcana.LOGGER.println("Map loaded in " + ((endTime - startTime) / 1000000) + " milliseconds");
				mapLoaded = true;
			}
		};
	}
	
	public void render(Graphics g) {
		if(backgroundAnim != null)
			backgroundAnim.render(g, new Rectangle(0, 0, Arcana.frame.getContentPane().getWidth(), Arcana.frame.getContentPane().getHeight()));
		
		
	}

	public void tick() {
		
	}
	
	public String getMapName() {
		return "This is a test map.";
	}
	
	public Point2D[] getSpawnLocations() {
		return new Point2D[]{new Point2D.Double(0, 0)};
	}

	@Override
	public boolean mapLoaded() {
		return mapLoaded;
	}
	
}
