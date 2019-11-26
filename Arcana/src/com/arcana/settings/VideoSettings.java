package com.arcana.settings;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

public class VideoSettings {
	
	private static final String[] frameRates = {"Uncapped", "30 FPS", "60 FPS", "120 FPS", "144 FPS", "160 FPS", "165 FPS", "180 FPS", "240 FPS"};
	private static final String[] resolutions = {"853x480", "1280x720", "1366x768", "1600x900", "1920x1080", "2560x1440", "3840x2160"};
	//private static DisplayMode[] displayModes = {new DisplayMode(int wid, int hei, int bitDepth)};
	
	public static String[] getFrameRates(){
		return frameRates;
	}
	
	public static String[] getResolutions(){
		Dimension screenRes = getMaxScreenResolution();
		int width = (int) screenRes.getWidth();
		int height = (int) screenRes.getHeight();
		int bannedRes = 0;
		for(int i = 0; i < resolutions.length; i++){
			int resX = Integer.parseInt(resolutions[i].split("x")[0]);
			int resY = Integer.parseInt(resolutions[i].split("x")[1]);
			if(resX > width || resY > height)
				bannedRes++;
		}
		String[] supportedRes = new String[resolutions.length - bannedRes];
		for(int i = 0; i < supportedRes.length; i++){
			supportedRes[i] = resolutions[i];
		}
		return supportedRes;
	}
	
	public static Dimension getMaxScreenResolution(){
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = 0, height = 0;
		for(DisplayMode dm : gd.getDisplayModes()){
			if(dm.getWidth() * dm.getHeight() > width * height){
				width = dm.getWidth();
				height = dm.getHeight();
			}
		}
		return new Dimension(width, height);
	}
}
