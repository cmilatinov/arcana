package com.arcana.game;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.JFrame;

import com.arcana.anim.Animations;
import com.arcana.characters.Character;
import com.arcana.entities.Controller;
import com.arcana.entities.Entity;
import com.arcana.entities.EntityFireball;
import com.arcana.entities.EntityPlayer;
import com.arcana.entities.EntityPlayerMP;
import com.arcana.input.Keyboard;
import com.arcana.network.Client;
import com.arcana.network.GamePacket;
import com.arcana.network.NetworkManager;
import com.arcana.settings.Settings;
import com.arcana.src.Arcana;
import com.arcana.utils.Vector2d;

public class Game {
	
	private Controller c;
	public EntityPlayer player;
	public EntityPlayerMP opponent;
	private Map map;
	private JFrame frame;
	private Settings s;
	private NetworkManager netM;
	
	public Game(Settings s, JFrame frame, Character playerHero, Character opponentHero, Map map, NetworkManager netM){
		this.netM = netM;
		this.map = map;
		this.frame = frame;
		this.c = new Controller();
		this.s = s;
		Arcana.LOGGER.println("Loading \"" + map.getMapName() + "\"");
		new Thread(map.loadMap(this, c)).start();
		this.player = new EntityPlayer(playerHero, 0, 1);
		this.opponent = new EntityPlayerMP(opponentHero, 0, 1);
	}
	
	public Game(Settings s, JFrame frame, Character playerHero, Map map){
		this.map = map;
		this.frame = frame;
		this.c = new Controller();
		this.s = s;
		Arcana.LOGGER.println("Loading \"" + map.getMapName() + "\"");
		new Thread(map.loadMap(this, c)).start();
		this.player = new EntityPlayer(playerHero, 0, 0);
	}
	
	public void createOpponent(Character hero){
		this.opponent = new EntityPlayerMP(hero, 0, 0);
	}
	
	public void update(GamePacket p, NetworkManager netM){
		if(netM instanceof Client){
			double px = Double.parseDouble(p.getValue("pX"));
			double py = Double.parseDouble(p.getValue("pY"));
			double pvx = Double.parseDouble(p.getValue("pvX"));
			double pvy = Double.parseDouble(p.getValue("pvY"));
			boolean pisF = p.getValue("pisF").equals("true");;
			double ox = Double.parseDouble(p.getValue("oX"));
			double oy = Double.parseDouble(p.getValue("oY"));
			double ovx = Double.parseDouble(p.getValue("ovX"));
			double ovy = Double.parseDouble(p.getValue("ovY"));
			boolean oisF = p.getValue("oisF").equals("true");
			player.setPosition(ox, oy);
			player.isFalling = oisF;
			player.setVelocity(new Vector2d(ovx, ovy));
			opponent.setPosition(px, py);
			opponent.isFalling = pisF;
			opponent.setVelocity(new Vector2d(pvx, pvy));
			opponent.setCurrentAnimation(p.getValue("anim"));
			opponent.setCurrentAnimationFrame(Integer.parseInt(p.getValue("animF")));
			updateEntities(p.getVariableNamesStartingWith("e|"), p);
		}
	}
	
	private void updateEntities(List<String> serverEntities, GamePacket p) throws ArrayIndexOutOfBoundsException{
		if(serverEntities.size() == 0) 
			return;
		HashMap<String, Entity> entities = Arcana.getGame().getController().getAllEntities();
		List<String> clientEntities = new ArrayList<String>(entities.keySet());
		clientEntities.removeAll(serverEntities);
		for(String str : clientEntities){
			
				Arcana.getGame().getController().removeEntity(str);
		}
		for(String str : serverEntities){
			String name = str.split("\\|")[1];
			String[] values = p.getValue(str).split(",");
			if(Arcana.getGame().getController().entityExists(name)){
				if(values[0].equals("fireball")){
					EntityFireball fire = ((EntityFireball)Arcana.getGame().getController().getEntity(name));
					fire.setPosition(Double.parseDouble(values[1]), Double.parseDouble(values[2]));
					fire.setVelocityX(Double.parseDouble(values[3]));
					fire.setVelocityY(Double.parseDouble(values[4]));
				}
			}else if(values[0].equals("fireball")){
				EntityFireball fire = new EntityFireball(Double.parseDouble(values[1]), 
						Double.parseDouble(values[2]), 
						Double.parseDouble(values[3]), 
						Double.parseDouble(values[4]),
						Double.parseDouble(values[3]) > 0.0D ? Animations.getAnim("FireballAnimRight") 
								: Animations.getAnim("FireballAnimLeft"), false);
				Arcana.getGame().getController().registerEntity(name, fire);
			}
		}
		
		
	}
	
	public Controller getController(){
		return c;
	}
	
	public void loadControls(){
		/*
		s.getKeyBindings().setAction("walkLeft", new Runnable(){
			public void run(){
				player.walkLeft();
			}
		});
		s.getKeyBindings().setAction("walkRight", new Runnable(){
			public void run(){
				player.walkRight();
			}
		});
		*/
		s.getKeyBindings().setAction("crouch", new Runnable(){
			public void run(){
				player.crouch();
			}
		});
		s.getKeyBindings().setReleaseAction("crouch", new Runnable(){
			public void run(){
				player.uncrouch();
			}
		});
		s.getKeyBindings().setAction("jump", new Runnable(){
			public void run(){
				player.jump();
				if(netM instanceof Client)
					((Client)netM).jump();
			}
		});
		s.getKeyBindings().setAction("ability1", new Runnable(){
			public void run(){
				String uuid = UUID.randomUUID().toString().split("-")[0];
				player.ability1(uuid);
				if(netM instanceof Client)
					((Client)netM).ability1(uuid);
			}
		});
	}
	
	public void render(Graphics g){
		if(map.mapLoaded()){
			map.render(g);
			player.render(g);
			opponent.render(g);
			c.render(g);
		}else{
			//TODO LOADING SCREEN
		}
	}
	
	private void checkPlayerMovement(){
		double vel = 0;
		if(Keyboard.isKeyDown(s.getKeyBindings().getKeyCode("walkLeft")))
			vel -= player.getRunSpeed();
		if(Keyboard.isKeyDown(s.getKeyBindings().getKeyCode("walkRight")))
			vel += player.getRunSpeed();
		if(player.getVelocity().getX() != vel){
			if(vel < 0){
				player.walkLeft();
				if(netM instanceof Client)
					((Client)netM).walkLeft();
			}else if(vel > 0){ 
				player.walkRight();
				if(netM instanceof Client)
					((Client)netM).walkRight();
			}else if(vel == 0){
				player.stopWalking();
				if(netM instanceof Client)
					((Client)netM).stopMoving();
			}
		}
	}
	
	public void tick(){
		if(map.mapLoaded()){
			map.tick();
			player.tick();
			opponent.tick();
			checkPlayerMovement();
			c.tick();
		}else{
			//TODO LOADING SCREEN
		}
	}
	
	public EntityPlayer getPlayer(){
		return player;
	}
	
	public EntityPlayerMP getOpponent(){
		return opponent;
	}
	
	public void registerEntity(String name, Entity e){
		c.registerEntity(name, e);
	}
}
