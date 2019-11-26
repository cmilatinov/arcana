package com.arcana.utils;

import com.arcana.game.Game;
import com.arcana.network.NetworkManager;
import com.arcana.network.Server;
import com.arcana.src.Arcana;
import com.utils.src.Logger;

public class Commands {
	
	private Game g; 
	private NetworkManager netM;
	
	private static final String[] commands = {
			"jump",
			"moveLeft",
			"moveRight",
			"stopMovement",
			"ability1",
			"ability2",
			"removeEntity"
	};
	
	public Commands(Game g, NetworkManager netM){
		this.g = g;
		this.netM = netM;
	}
	
	public void executeCommand(String command){
		try{
			String c = command;
			if(c.contains("("))
				c = c.split("\\(")[0];
			String args = "";
			if(command.contains("("))
				args = command.split("\\(")[1].split("\\)")[0];
			if(c.equals(commands[0])){
				jump();
			}else if(c.equals(commands[1])){
				moveLeft();
			}else if(c.equals(commands[2])){
				moveRight();
			}else if(c.equals(commands[3])){
				stopMovement();
			}else if(c.equals(commands[4])){
				ability1(args);
			}
		}catch(NullPointerException e){
			Arcana.LOGGER.println("Could not run the following command : \"" + command + "\"", Logger.WARNING);
		}
	}
	
	private void jump(){
		if(netM instanceof Server)
			g.getOpponent().jump();
	}
	
	private void moveLeft(){
		if(netM instanceof Server)
			g.getOpponent().walkLeft();
	}
	
	private void moveRight(){
		if(netM instanceof Server)
			g.getOpponent().walkRight();
	}
	
	private void stopMovement(){
		if(netM instanceof Server)
			g.getOpponent().stopWalking();
	}
	
	private void ability1(String args){
		if(netM instanceof Server)
			g.getOpponent().ability1(args);
	}
}
