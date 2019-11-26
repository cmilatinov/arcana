package com.arcana.sound;

import java.util.ArrayList;

import com.arcana.audio.AudioSample;

public class Sounds {
	
	private static ArrayList<AudioSample> sfx = new ArrayList<AudioSample>();
	private static ArrayList<String> sfxNames = new ArrayList<String>();
	private static ArrayList<AudioSample> music = new ArrayList<AudioSample>();
	private static ArrayList<String> musicNames = new ArrayList<String>();
	
	private static float sfxVol = 1.0f;
	private static float musicVol = 1.0f;
	
	public static AudioSample getSoundEffect(String name){
		for(int i = 0; i < sfxNames.size() ; i++){
			if(sfxNames.get(i).equals(name)){
				return sfx.get(i);
			}
		}
		return null;
	}
	
	public static AudioSample getMusic(String name){
		for(int i = 0; i < musicNames.size() ; i++){
			if(musicNames.get(i).equals(name)){
				return music.get(i);
			}
		}
		return null;
	}
	
	public static void addMusic(String name, AudioSample a){
		if(getMusic(name) != null) return;
		musicNames.add(name);
		a.setVolume(musicVol);
		music.add(a);
	}
	
	public static void addSoundEffect(String name, AudioSample a){
		if(getSoundEffect(name) != null) return;
		sfxNames.add(name);
		a.setVolume(sfxVol);
		sfx.add(a);
	}
	
	public static void setSfxVolume(float vol){
		sfxVol = vol;
		for(AudioSample sf : sfx)
			sf.setVolume(sfxVol);
	}
	
	public static void setMusicVolume(float vol){
		musicVol = vol;
		for(AudioSample sample : music)
			sample.setVolume(musicVol);
	}
}
