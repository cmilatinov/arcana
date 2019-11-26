package com.arcana.audio;

import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.arcana.src.Arcana;
import com.utils.src.Logger;

/**
 * Class used for the playback of a specific sound.
 */
public class AudioPlayer extends Thread{
	
	/**
	 * The audio line on which the sounds is to be played.
	 */
	private SourceDataLine line;
	
	/**
	 * The audio sample to be played.
	 */
	private AudioSample sample;
	
	/**
	 * The buffer size used for playback.
	 */
	private final int bufferSize = 1000;
	
	/**
	 * The audio input stream used a the source.
	 */
	private AudioInputStream ais;
	
	/**
	 * Whether or not the sound is currently playing.
	 */
	private boolean play = true;
	
	/**
	 * The volume control associated with the source data line.
	 */
	private FloatControl volCtrl;
	
	/**
	 * Protected constructor that can only be called through AudioLibrary.playSound().
	 * @param sample The audio sample to be played.
	 */
	protected AudioPlayer(AudioSample sample){
		try {
			this.sample = sample;
			this.ais = AudioSystem.getAudioInputStream(sample.getFile());
			this.line = AudioSystem.getSourceDataLine(ais.getFormat(), AudioLibrary.getCurrentAudioDevice());
			line.open(ais.getFormat());
			this.volCtrl = (FloatControl)line.getControl(FloatControl.Type.MASTER_GAIN);
			this.start();
		} catch (LineUnavailableException | UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Stops the playback of the sound and removes it from the currently playing sounds.
	 * @return [void]
	 */
	public void stopPlayback(){
		this.play = false;
		AudioLibrary.removeSound(this);
	}
	
	/**
	 * The run method of the thread playing the sound.
	 * @return [void]
	 */
	public void run(){
		try {
			line.start();
			volCtrl.setValue(convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()));
			int nBytesRead = 0;
			byte[] buffer = new byte[bufferSize];
			while((nBytesRead = ais.read(buffer)) != -1 && play == true){
				if(convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) != volCtrl.getValue() && convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) <= 6.0206f && convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) >= -80.0f)
					volCtrl.setValue(convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()));
				else if(convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) != volCtrl.getValue() && convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) > 6.0206f){
					volCtrl.setValue(6.0206f);
				}else if(convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) != volCtrl.getValue() && convertFloatToDb(AudioLibrary.getMasterVolume() * sample.getVolume()) < -80.0f)
					volCtrl.setValue(-80.0f);
				
				line.write(buffer, 0, nBytesRead);
			}
			line.drain();
			line.close();
			AudioLibrary.removeSound(this);
		} catch (IOException e) {
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	/**
	 * Converts a linear volume value to the decibel scale.
	 * @param vol The volume value from 0.0 to 1.0.
	 * @return [float] The corresponding decibel scale value.
	 */
	private float convertFloatToDb(float vol){
		float dB = (float) (20f * Math.log(vol));
		return dB > 6.0206f ? 6.0206f : dB;
	}
	
	/**
	 * Whether or not the sound is currently playing.
	 * @return [boolean] The boolean value of the above expression.
	 */
	public boolean isPlaying(){
		return this.play;
	}
}
