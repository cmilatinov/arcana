package com.arcana.audio;

import java.io.File;
import java.util.ArrayList;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Mixer;

/**
 * Class used to manage audio playback within the game.
 */
public class AudioLibrary {
	
	/**
	 * Sound device to use for audio output.
	 */
	private static Mixer.Info soundDevice = AudioSystem.getMixerInfo()[0];
	
	/**
	 * Master volume on a linear scale from 0.0 to 1.0.
	 */
	private static float volume = 1.0f;
	
	/**
	 * Array of sounds that are currently playing.
	 */
	public static ArrayList<AudioPlayer> soundsPlaying = new ArrayList<AudioPlayer>();
	
	/**
	 * Retrieves all audio devices that can be used for sound playback.
	 * @return [Mixer.Info[]] The list of audio devices.
	 */
	public static Mixer.Info[] getSoundDevices(){
		Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
		int numDevices = 0;
		for(Mixer.Info info : mixerInfo){
			if(info.getName().contains("Primary Sound Capture Driver"))
				break;
			else{
				numDevices++;
			}
		}
		Mixer.Info[] playbackDevices = new Mixer.Info[numDevices];
		for(int i = 0; i < playbackDevices.length; i++){
			playbackDevices[i] = mixerInfo[i];
		}
		return playbackDevices;
	}
	
	/**
	 * Retrieves an audio device using its name.
	 * @param name The name of the device to search for.
	 * @return [Mixer.Info] The audio device or null if none is found.
	 */
	public static Mixer.Info getDeviceByName(String name){
		Mixer.Info[] devices = getSoundDevices();
		Mixer.Info device = null;
		for(Mixer.Info soundDevice : devices){
			if(soundDevice.getName().contains(name)){
				device = soundDevice;
				break;
			}
		}
		return device;
	}
	
	/**
	 * Retrieves the master volume on a linear scale from 0.0 to 1.0.
	 * @return [float] The master volume.
	 */
	public static float getMasterVolume(){
		return volume;
	}
	
	/**
	 * Sets the master volume on a linear scale from 0.0 to 1.0.
	 * @param vol The volume at which to play the sounds.
	 * @return [void]
	 */
	public static void setMasterVolume(float vol){
		volume = vol;
	}


	/**
	 * Retrieves the current audio device used for sound playback.
	 * @return [Mixer.Info] The audio device.
	 */
	public static Mixer.Info getCurrentAudioDevice(){
		return soundDevice;
	}
	
	/**
	 * Set the audio device to use for sound playback.
	 * @return [void]
	 */
	public static void setAudioDevice(Mixer.Info device){
		for(int i = 0; i < soundsPlaying.size(); i++){
			AudioPlayer p = soundsPlaying.get(i);
			p.stopPlayback();
			soundsPlaying.remove(p);
		}
		if(device != null)
			soundDevice = device;
	}
	
	/**
	 * Creates an audio sample using the specified file.
	 * @param filePath The path to the file.
	 * @return [AudioSample] The audio sample.
	 */
	public static AudioSample createSound(String filePath){
		return new AudioSample(new File(filePath));
	}
	
	
	/**
	 * Plays an audio sample on the current audio device.
	 * @param sample The audio sample.
	 * @return [AudioPlayer] An AudioPlayer object used to control playback.
	 */
	public static AudioPlayer playSound(AudioSample sample){
		AudioPlayer p = new AudioPlayer(sample);
		soundsPlaying.add(p);
		return p;
	}
	
	/**
	 * Removes a sound from the 'currently playing' list, essentially stopping playback.
	 * @param player The AudioPlayer object that is currently playing the sound.
	 * @return [void]
	 */
	protected static void removeSound(AudioPlayer player){
		soundsPlaying.remove(player);
	}
}
