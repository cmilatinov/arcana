package com.arcana.settings;

import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.arcana.src.Arcana;


public class Keybindings {
	
	private ArrayList<String> names = new ArrayList<String>();
	private ArrayList<Integer> keycodes = new ArrayList<Integer>();
	private ArrayList<Runnable> actions = new ArrayList<Runnable>();
	private ArrayList<Runnable> releaseActions = new ArrayList<Runnable>();
	
	private Settings s;
	
	public Keybindings(Settings s){
		this.s = s;
	}
	
	public int addKeybinding(String name, int keycode){
		names.add(name);
		keycodes.add(keycode);
		actions.add(new Runnable(){
			public void run(){
			}
		});
		releaseActions.add(new Runnable(){
			public void run(){
			}
		});
		Arcana.LOGGER.println("Registering keybinding \"" + name + "\" with keycode " + keycode);
		return getIndex(name, names);
	}
	
	public void changeKeyBinding(String name, int newKeyCode){
		for(int i = 0; i < names.size(); i++){
			if(names.get(i).equals(name)){
				keycodes.set(i, newKeyCode);
				s.set("Keybindings", name, Integer.toString(newKeyCode));
			}
		}
	}
	
	public void setAction(String name, Runnable action){
		int index = getIndex(name, names);
		actions.set(index, action);
		
	}
	
	public void setReleaseAction(String name, Runnable action){
		int index = getIndex(name, names);
		releaseActions.set(index, action);
	}
	
	public void executeAction(int keycode){
		try{
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for(int i  = 0; i < keycodes.size(); i++){
				if(keycodes.get(i) == keycode)
					indexes.add(i);
			}
			for(int i  = 0; i < indexes.size(); i++){
				Runnable action = actions.get(indexes.get(i));
				action.run();
			}
		}catch(IndexOutOfBoundsException e){
			return;
		}
		
	}
	
	public void executeReleaseAction(int keycode){
		try{
			ArrayList<Integer> indexes = new ArrayList<Integer>();
			for(int i  = 0; i < keycodes.size(); i++){
				if(keycodes.get(i) == keycode)
					indexes.add(i);
			}
			for(int i  = 0; i < indexes.size(); i++){
				Runnable action = releaseActions.get(indexes.get(i));
				action.run();
			}
		}catch(IndexOutOfBoundsException e){
			return;
		}
		
	}
	
	private int getIndex(Object obj, @SuppressWarnings("rawtypes") ArrayList list){
		for(int i = 0; i < list.size(); i++){
			if(list.get(i).equals(obj))
				return i;
		}
		return -1;
	}
	
	public String[] getAllKeybindings(){
		String[] keys = new String[names.size()];
		for(int i = 0; i < keys.length; i++){
			keys[i] = splitAtUpperCase(names.get(i));
		}
		return keys;
	}
	
	public String[] getAllKeyStrokes(){
		String[] keys = new String[keycodes.size()];
		for(int i = 0; i < keys.length; i++){
			keys[i] = KeyEvent.getKeyText(keycodes.get(i));
		}
		return keys;
	}
	
	public String splitAtUpperCase(String str){
		String res = "";
		for(int i = 0; i < str.length(); i++){
			Character ch = str.charAt(i);
			if(i == 0){
				ch = Character.toUpperCase(ch); 
				res += ch;
			}else if(Character.isUpperCase(ch))
				res += " " + ch;
			else if(!Character.isAlphabetic(ch))
				res += " " + ch;
			else 
				res += ch;
		}
		return res;
	}
	
	public int getKeyCode(String keybinding){
		int index = getIndex(keybinding, names);
		return keycodes.get(index);
	}
	
}
