package com.arcana.entities;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ConcurrentModificationException;
import java.util.HashMap;

import com.arcana.src.Arcana;
import com.arcana.src.Display;

public class Controller {
	
	private boolean drawHitboxes = false;
	private HashMap<String, Entity> entities = new HashMap<String, Entity>();
	
	public Controller(){
		if(Integer.parseInt(Arcana.getSettings().get("Frame", "drawHitboxes")) == 1)
			drawHitboxes = true;
	}
	
	public Entity getEntity(String name){
		return entities.get(name);
	}
	
	public void removeEntity(Entity e){
		for(String key : entities.keySet()){
			if(entities.get(key).equals(e)){
				entities.remove(key, entities.get(key));
				break;
			}
		}
	}
	
	public boolean entityExists(String name){
		return entities.containsKey(name) && entities.get(name) != null;
	}
	
	public void removeEntity(String key){
		entities.remove(key, entities.get(key));
	}
	
	public void registerEntity(String name, Entity e){
		entities.put(name, e);
	}
	
	public HashMap<String, Entity> getAllEntities(){
		return entities;
	}
	
	public void registerEntities(String[] names, Entity[] e){
		for(int i = 0; i < names.length; i++){
			entities.put(names[i], e[i]);
		}
	}
	
	public void tick(){
		try{
			for(String key: entities.keySet())
				entities.get(key).tick();
		}catch(ConcurrentModificationException e){}
	}
	
	public void render(Graphics g){
		try{
			for(String key: entities.keySet()){
				Entity e = entities.get(key);
				if(drawHitboxes){
					Graphics2D g2d = (Graphics2D) g;
					g.setColor(Color.black);
					g2d.draw(Display.convertRectToScreen(new Rectangle2D.Double(e.getPosition().getX(), e.getPosition().getY(), e.getBounds().getWidth(), e.getBounds().getHeight())));
				}
				e.render(g);
			}
		}catch(ConcurrentModificationException | NullPointerException e){}
	}
}
