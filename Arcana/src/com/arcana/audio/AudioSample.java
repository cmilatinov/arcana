package com.arcana.audio;

import java.io.File;

/**
 * Class used to represent an audio sample taken from a file.
 */
public class AudioSample {
		
	/**
	 * The file in which the audio sample is stored.
	 */
	private File file;
	
	/**
	 * The volume on a linear scale from 0.0 to 1.0 at which to play the sound.
	 */
	private float volume = 1.0f;
	
	/**
	 * Constructor that takes in the audio file.
	 * @param f The audio file.
	 */
	protected AudioSample(File f){
		this.file = f;
	}
	
	/**
	 * Returns the file in which the audio is stored.
	 * @return [File] The audio file.
	 */
	public File getFile(){
		return file;
	}
	
	/**
	 * Sets the volume at which the sound is meant to be played.
	 * @param vol The volume on a linear scale from 0.0 to 1.0.
	 * @return [void]
	 */
	public void setVolume(float vol){
		this.volume = vol;
	}

	/**
	 * Returns the volume at which the sound is meant to be played.
	 * @return [float] The volume on a linear scale from 0.0 to 1.0.
	 */
	public float getVolume(){
		return volume;
	}
	
}
