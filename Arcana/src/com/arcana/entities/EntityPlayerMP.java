package com.arcana.entities;

import java.awt.Graphics;

import javax.swing.JFrame;

import com.arcana.characters.Character;
import com.arcana.network.Server;
import com.arcana.src.Arcana;

public class EntityPlayerMP extends EntityPlayer{
	
	private String currentAnimation = "";
	private int currentAnimationFrame = 0;
	
	public EntityPlayerMP(Character character, double x, double y) {
		super(character, x, y);
	}
	
	public int getCurrentAnimationFrame() {
		return currentAnimationFrame;
	}

	public void setCurrentAnimationFrame(int currentAnimationFrame) {
		this.currentAnimationFrame = currentAnimationFrame;
	}

	public String getCurrentAnimation() {
		return currentAnimation;
	}

	public void setCurrentAnimation(String currentAnimation) {
		this.currentAnimation = currentAnimation;
	}

	public void render(Graphics g){
		if(Arcana.getNetworkManager() instanceof Server)
			super.render(g);
		else{
			super.getCharacter().renderAnim(currentAnimation, currentAnimationFrame, g, x, y);
		}
	}
	
	public void tick(){
		super.tick();
	}
}
