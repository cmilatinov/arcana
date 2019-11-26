package com.arcana.loaders;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageLoader {
	
	/**
	 * Reads an image from the disk and returns it as a BufferedImage.
	 * @param path The path of the image on disk.
	 * @return An instance of BufferedImage that can be drawn using Graphics.
	 * @throws IOException
	 */
	public static BufferedImage loadImage(String path) throws IOException{
		return ImageIO.read(new File(path));
	}
	
	/**
	 * Takes part of an image and returns it as a separate BufferedImage.
	 * @param img The image acting as a spritesheet.
	 * @param r The Rectangle in which the desired image lies.
	 * @return An instance of BufferedImage that can be drawn using Graphics.
	 */
	public static BufferedImage getSprite(BufferedImage img, Rectangle r){
		return img.getSubimage((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}
	
	/**
	 * Mirrors an image horizontally.
	 * @param imgage The image to be mirrored horizontally.
	 * @return An instance of BufferedImage that can be drawn using Graphics.
	 */
	public static BufferedImage flipHoriz(BufferedImage image) {
	    BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    Graphics2D gg = newImage.createGraphics();
	    gg.drawImage(image, image.getHeight(), 0, -image.getWidth(), image.getHeight(), null);
	    gg.dispose();
	    return newImage;
	}
}
