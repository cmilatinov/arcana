package com.arcana.anim;

import java.util.HashMap;

/**
 * Class used to manage all in-game animations.
 */
public class Animations {
	
	/**
	 * Static HashMap of animations.
	 */
	private static HashMap<String, Animation> anims = new HashMap<String, Animation>();
	
	
	/**
	 * Searches for an animation using its registered name.
	 * @param name The name of the animation to search for.
	 * @return [Animation] The requested animation or null if none is found.
	 */
	public static Animation getAnim(String name){
		return anims.get(name);
	}
	
	/**
	 * Registers an animation with a specific name that can be used to retrieve it later.
	 * @param name The name to register the animation with.
	 * @param anim The Animation object.
	 * @return [void]
	 */
	public static void addAnim(String name, Animation anim){
		anims.put(name, anim);
	}
}
