package com.arcana.settings;

import java.io.IOException;
import java.util.ArrayList;

import com.arcana.audio.AudioLibrary;
import com.arcana.sound.Sounds;
import com.arcana.src.Arcana;
import com.utils.ini.INI;
import com.utils.ini.INIHeader;
import com.utils.ini.INIVar;
import com.utils.src.Logger;

public class Settings {
	
	private final String configPath = "/cfg/config.cfg";
	
	private String jarPath = Arcana.getJarPath();
	
	private INI config;
	
	private Keybindings keyBinds = new Keybindings(this);
	
	/**
	 * Initializing the settings class which we will use to dynamically change the settings in game.
	 */
	public Settings(){
		try {
			config = new INI(jarPath + configPath);
			if(!config.contains("Frame", "width") ||
				!config.contains("Frame", "height") ||
				!config.contains("Frame", "fullscreen") ||
				!config.contains("Frame", "showFPS") ||
				!config.contains("Frame", "maxFPS") ||
				!config.contains("Frame", "drawHitboxes") ||
				!config.contains("Keybindings", "jump") ||
				!config.contains("Keybindings", "walkLeft") ||
				!config.contains("Keybindings", "walkRight") ||
				!config.contains("Keybindings", "crouch") ||
				!config.contains("Keybindings", "ability1") ||
				!config.contains("Keybindings", "ability2") ||
				!config.contains("Keybindings", "ability3") ||
				!config.contains("Keybindings", "menuMoveUp") ||
				!config.contains("Keybindings", "menuMoveLeft") ||
				!config.contains("Keybindings", "menuMoveRight") ||
				!config.contains("Keybindings", "menuMoveDown") ||
				!config.contains("Keybindings", "select") ||
				!config.contains("Audio", "masterVolume") ||
				!config.contains("Audio", "sfxVolume") ||
				!config.contains("Audio", "musicVolume") ||
				!config.contains("Audio", "soundDevice") ||
				!config.contains("Network", "username") ||
				!config.contains("Network", "serverIP")){
				Arcana.LOGGER.println("Config file corrupt or missing, using default config", Logger.WARNING);
				config = DefaultConfig.setDefaultConfigurationFile(new INI(jarPath + configPath, false));
			}
		} catch (IOException e) {
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem: e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	/**
	 * Retrieves a value from the configuration file.
	 * @param header The ini header under which the variable is listed.
	 * @param varName The name of the variable.
	 * @return The desired value.
	 */
	public String get(String header, String varName){
		return config.get(header, varName);
	}
	
	/**
	 * Writes the configuration file on disk so it can be used next time we boot up the game.
	 */
	public void saveSettings(){
		try {
			config.set("Audio", "masterVolume", Float.toString(AudioLibrary.getMasterVolume()));
			config.set("Audio", "soundDevice", AudioLibrary.getCurrentAudioDevice().getName());
			config.writeFile();
		} catch (IOException e) {
			Arcana.LOGGER.println("Couldn't properly save settings", Logger.WARNING);
			for(StackTraceElement elem: e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.WARNING);
		}
	}
	
	/**
	 * Goes through the list of key bindings in the config file and adds them to the Keybindings class.
	 */
	public void registerKeyBindings(){
		try{
			INIHeader h = config.getHeader("Keybindings");
			if(h == null) return;
			ArrayList<INIVar> vars = (ArrayList<INIVar>) h.getVars();
			for(INIVar var : vars){
				String name = var.getName();
				int keycode = Integer.parseInt(var.getValue());
				keyBinds.addKeybinding(name, keycode);
			}
		}catch (Exception e){
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
			try {
				Arcana.LOGGER.println("Config file corrupt, using default config", Logger.WARNING);
				config = DefaultConfig.setDefaultConfigurationFile(new INI(jarPath + configPath, false));
				keyBinds = new Keybindings(this);
				registerKeyBindings();
			} catch (IOException e1) {
				Arcana.LOGGER.println(e1.toString(), Logger.ERROR);
				for(StackTraceElement elem : e1.getStackTrace())
					Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
			}
		}
	}
	
	public void loadDefaultConfig(){
		try {
			Arcana.LOGGER.println("Config file corrupt, using default config", Logger.WARNING);
			config = DefaultConfig.setDefaultConfigurationFile(new INI(jarPath + configPath, false));
			registerKeyBindings();
		} catch (IOException e1) {
			Arcana.LOGGER.println(e1.toString(), Logger.ERROR);
			for(StackTraceElement elem : e1.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	/**
	 * Retrieves the key bindings contained in the configuration file.
	 * @return An instance of the Keybindings class containing the key binds parsed from the configuration file.
	 */
	public Keybindings getKeyBindings(){
		return keyBinds;
	}
	
	public void set(String header, String varName, String value){
		config.set(header, varName, value);
	}
}
