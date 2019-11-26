package com.arcana.settings;

import java.io.IOException;

import com.arcana.src.Arcana;
import com.utils.ini.INI;
import com.utils.src.Logger;

public class DefaultConfig {
	
	/**
	 * The default configuration file for the game.
	 * @param file
	 * @return
	 */
	
	public static INI setDefaultConfigurationFile(INI file){
		file.set("Frame", "width", "1280");
		file.set("Frame", "height", "720");
		file.set("Frame", "fullscreen", "0");
		file.set("Frame", "showFPS", "0");
		file.set("Frame", "maxFPS", "0");
		file.set("Frame", "drawHitboxes", "1");
		
		file.set("Keybindings", "jump", "87");
		file.set("Keybindings", "walkLeft", "65");
		file.set("Keybindings", "walkRight", "68");
		file.set("Keybindings", "crouch", "83");
		file.set("Keybindings", "ability1", "49");
		file.set("Keybindings", "ability2", "50");
		file.set("Keybindings", "ability3", "51");
		file.set("Keybindings", "menuMoveUp", "87");
		file.set("Keybindings", "menuMoveLeft", "65");
		file.set("Keybindings", "menuMoveRight", "68");
		file.set("Keybindings", "menuMoveDown", "83");
		file.set("Keybindings", "select", "10");
		
		file.set("Audio", "masterVolume", "1.0");
		file.set("Audio", "sfxVolume", "1.0");
		file.set("Audio", "musicVolume", "1.0");
		file.set("Audio", "soundDevice", "Primary Sound Driver");
		
		file.set("Network", "username", "Player 1");
		file.set("Network", "serverIP", "127.0.0.1");
		
		try {
			file.writeFile();
		} catch (IOException e) {
			for(StackTraceElement elem: e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.WARNING);
		}
		return file;
	}
	
	
	
}
