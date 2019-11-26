package com.arcana.input;

import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import com.arcana.menus.KeyBindingsMenu;
import com.arcana.settings.Keybindings;

public class Keyboard implements KeyListener {
	
	private Keybindings k;
	private KeyBindingsMenu m;
	private boolean listeningForKeyStroke = false;
	private static boolean[] keys = new boolean[600];
	
	public Keyboard(Keybindings k, KeyBindingsMenu m){
		this.k = k;
		this.m = m;
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher(){
			public boolean dispatchKeyEvent(KeyEvent arg0) {
				if(arg0.getID() == KeyEvent.KEY_PRESSED){
					keyPressed(arg0);
				}else if(arg0.getID() == KeyEvent.KEY_RELEASED){
					keyReleased(arg0);
				}
				return false;
			}
		});
	}
	
	public void keyPressed(KeyEvent e) {
		keys[e.getKeyCode()] = true;
		if(!listeningForKeyStroke){
			k.executeAction(e.getKeyCode());
		}else{
			m.nextKeyStroke(e);
			listeningForKeyStroke = false;
		}
	}

	public void keyReleased(KeyEvent e) {
		keys[e.getKeyCode()] = false;
		k.executeReleaseAction(e.getKeyCode());
	}

	public void keyTyped(KeyEvent e) {

	}
	
	public void listenForNextKeyStroke(){
		listeningForKeyStroke = true;
	}
	
	public static boolean isKeyDown(int keycode){
		return keys[keycode];
	}
}
