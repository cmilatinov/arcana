package com.arcana.src;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.image.BufferStrategy;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

import javax.swing.JFrame;

import com.arcana.audio.AudioLibrary;
import com.arcana.game.Game;
import com.arcana.input.Keyboard;
import com.arcana.input.Mouse;
import com.arcana.menus.AudioMenu;
import com.arcana.menus.KeyBindingsMenu;
import com.arcana.menus.Menu;
import com.arcana.menus.OptionsMenu;
import com.arcana.menus.VideoMenu;
import com.arcana.menus.Menu.MenuState;
import com.arcana.network.Client;
import com.arcana.network.NetworkManager;
import com.arcana.network.Server;
import com.arcana.settings.Settings;
import com.arcana.sound.Sounds;
import com.utils.src.Logger;

/**
 * Description of class.
 * */
public class Arcana extends Canvas implements Runnable{
	
	private static final long serialVersionUID = -7958280528128251008L;
	public static final String TITLE = "Arcana";
	public static final double VERSION = 1.0;
	
	public static final Logger LOGGER = new Logger(getJarPath() + "/logs/", new SimpleDateFormat("MM-dd-YYYY [HHmmss]"));
	
	public static JFrame frame;
	public static boolean running = false;
	
	private int FPS;
	private Font fpsFont;
	
	private static Thread mainGameThread;
	
	private static Settings settings;
	private Menu menu;
	
	public static final int ticksPerSec = 60;
	public static final int networkTicksPerSec = 30;
	public static int fpsCap = 0;
	
	public static double ACCELERATION = -0.2D/108D;
	public static double TERMINAL_VELOCITY = 4D;
	
	public static State gameState = State.Menu;
	private static Game game;
	private static NetworkManager netM;
	
	public static int fullscreen;
	
	private int fps = 0;
	private int ticks = 0;
	
	/**
	 * Creates the frame and starts the game.
	 */
	public static void main(String[] args){
		Arcana a = new Arcana();
		settings = new Settings();
		setupSettings(a);
		a.startGame();
	}
	
	/**
	 * @return A long timestamp of the current time in milliseconds.
	 */
	public static long timestamp(){
		return Timestamp.from(Instant.now()).getTime();
	}
	
	private static void setupSettings(Arcana a){
		try{
			fullscreen = Integer.parseInt(settings.get("Frame", "fullscreen"));
		}catch(Exception e){
			fullscreen = 0;
			settings.set("Frame", "fullscreen", "0");
		}
		try{
			frame = Display.createFrame(TITLE + " v" + VERSION, 
				new Dimension(Integer.parseInt(settings.get("Frame", "width")),  
				Integer.parseInt(settings.get("Frame", "height"))), false, fullscreen, a);
		}catch(Exception e){
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
			settings.loadDefaultConfig();
			frame = Display.createFrame(TITLE + " v" + VERSION, 
					new Dimension(Integer.parseInt(settings.get("Frame", "width")),  
					Integer.parseInt(settings.get("Frame", "height"))), false, fullscreen, a);
		}
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				Arcana.exitGame(0);
			}
		});
		LOGGER.println("Successfully created frame with dimensions " + frame.getWidth() + " by " + frame.getHeight());
	}
	
	/**
	 * Starts the game loop.
	 */
	private synchronized void startGame(){
		if(running) 
			return;
		if(mainGameThread == null)
			mainGameThread = new Thread(this);
		running = true;
		mainGameThread.start();
	}
	
	/**
	 * Stops the game loop.
	 */
	private synchronized static void stopGame(){
		if(!running) 
			return;
		try {
			running = false;
			mainGameThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * The main game loop of the application.
	 */
	public void run(){
		init();
		long lastTime = System.nanoTime();
		long lastUpdate = System.nanoTime();
		long lastNetMUpdate = System.nanoTime();
		new RenderThread().start();
		while(running){
			long currentTime = System.nanoTime();
			if(currentTime - lastTime >= 1000000000 / ticksPerSec){
				ticks++;
				tick();
			//	if(netM != null)
			//		netM.tick();
				lastTime = currentTime;
			}
			long currentNetMUpdate = System.nanoTime();
			if(currentNetMUpdate - lastNetMUpdate >= 1000000000 / networkTicksPerSec){
				if(netM != null)
					netM.tick();
				lastNetMUpdate = currentNetMUpdate;
			}
			long currentUpdate = System.nanoTime();
			if(currentUpdate - lastUpdate >= 1000000000){
				if(netM != null && netM.isConnected()){
					LOGGER.println("FPS: " + fps + ", TICKS: " + ticks + ", PING: " + netM.getPing() + " ms");
					LOGGER.println("PPS: " + netM.getPacketsReceived() + ", RECEIVED : " + netM.getBytesReceived()
					+ " B, SENT : " + netM.getBytesSent() + " B");
				}else
					LOGGER.println("FPS: " + fps + ", TICKS: " + ticks);
				
				this.FPS = fps;
				fps = 0; ticks = 0;
				
				lastUpdate = currentUpdate;
			}
			
		}
		stopGame();
	}
	
	private class RenderThread extends Thread{
		public void run(){
			long lastFps = System.nanoTime();
			while(Arcana.running){
				long currentFps = System.nanoTime();
				if(fpsCap != 0 && currentFps - lastFps >= 1000000000 / fpsCap){
					fps++;
					render();
					lastFps = currentFps;
				}else if(fpsCap == 0){
					fps++;
					render();
				}
			}
		}
	}
	
	/**
	 * Initializes the default values needed for the game.
	 */
	private void init(){
		long startTime = System.nanoTime();
		LOGGER.println("Initializing " + TITLE + " version " + VERSION);
		
		//FPS CAP
		try{
			fpsCap = Integer.parseInt(settings.get("Frame", "maxFPS"));
		}catch(Exception e){
			fpsCap = 0;
			settings.set("Frame", "maxFPS", "0");
		}
		
		//REGISTER MENU FONT
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		try {
			LOGGER.println("Registering fonts");
			ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File(getJarPath() + "/font/yoster.ttf")));
		} catch (FontFormatException | IOException e) {
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem: e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
		}
		
		//KEY BINDINGS
		settings.registerKeyBindings();
		registerKeyActions();
		
		//TODO GRAVITY 
		
		//AUDIO LIBRARY
		if(AudioLibrary.getDeviceByName(settings.get("Audio", "soundDevice")) == null){
			LOGGER.println("Invalid audio device, using system default", Logger.WARNING);
			settings.set("Audio", "soundDevice", "Primary Sound Driver");
		}else
			AudioLibrary.setAudioDevice(AudioLibrary.getDeviceByName(settings.get("Audio", "soundDevice")));
		LOGGER.println("Setting audio device to: " + AudioLibrary.getCurrentAudioDevice().getName());
		try{
			AudioLibrary.setMasterVolume(Float.parseFloat(settings.get("Audio", "masterVolume")));
			LOGGER.println("Setting master volume to: " + Integer.toString((int)(AudioLibrary.getMasterVolume() * 100)) + "%");
		}catch(Exception e){
			settings.set("Audio", "masterVolume", "1.0");
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
			LOGGER.println("Setting master volume to: " + Integer.toString((int)(AudioLibrary.getMasterVolume() * 100)) + "%");
		}
		try{
			LOGGER.println("Setting sound effects volume to: " + Integer.toString((int)(Float.parseFloat(settings.get("Audio", "sfxVolume")) * 100)) + "%");
			Sounds.setSfxVolume(Float.parseFloat(settings.get("Audio", "sfxVolume")));
		}catch(Exception e){
			Sounds.setSfxVolume(1.0f);
			settings.set("Audio", "sfxVolume", "1.0");
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
			LOGGER.println("Setting sound effects volume to: " + Integer.toString((int)(Float.parseFloat(settings.get("Audio", "sfxVolume")) * 100)) + "%");
		}
		try{
			LOGGER.println("Setting music volume to: " + Integer.toString((int)(Float.parseFloat(settings.get("Audio", "musicVolume")) * 100)) + "%");
			Sounds.setMusicVolume(Float.parseFloat(settings.get("Audio", "musicVolume")));
		}catch(Exception e){
			Sounds.setMusicVolume(1.0f);
			settings.set("Audio", "musicVolume", "1.0");
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
			LOGGER.println("Setting music volume to: " + Integer.toString((int)(Float.parseFloat(settings.get("Audio", "musicVolume")) * 100)) + "%");
		}
		try{
			initSounds();
		}catch(Exception e){
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
		}
		
		//MENU
		menu = new Menu(frame, settings);
		Keyboard k = new Keyboard(settings.getKeyBindings(), (KeyBindingsMenu) menu.options.submenus[0]);
		menu.options.setKeyboard(k);
			
		//LISTENERS
		Mouse m = new Mouse(menu);
		this.addMouseListener(m);
		this.addMouseWheelListener(m);
		this.requestFocus();
		//this.addKeyListener(k);
		
		//FPS
		fpsFont = new Font("Arial", Font.BOLD, frame.getContentPane().getHeight() * 4 / 108);
		
		
		long endTime = System.nanoTime();
		LOGGER.println("Initialization complete, time taken: " + Long.toString((endTime - startTime) / 1000000) + " milliseconds");
	}
	
	/**
	 * Initializes the sounds used in the game.
	 */
	private void initSounds(){
		//TODO INIT ALL SOUND EFFECTS AND MUSIC HERE
		Sounds.addMusic("Mi", AudioLibrary.createSound(getJarPath() + "/sound/swag.wav"));
	}
	
	/**
	 * Registers all of the key bindings used for the game.
	 */
	private void registerKeyActions(){
		//TODO add key actions here
		
		settings.getKeyBindings().setAction("menuMoveUp", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
					menu.moveCursorUp();
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = (AudioMenu)menu.options.submenus[2];
						if(amenu.state == AudioMenu.AudioMenuState.Audio)
							amenu.moveCursorUp();
						else if(amenu.state == AudioMenu.AudioMenuState.AudioDevice)
							amenu.adMenu.moveCursorUp();
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Options)
						menu.options.moveCursorUp();
					else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Video)
						((VideoMenu)menu.options.submenus[1]).moveCursorUp();
					else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.KeyBindings)
						((KeyBindingsMenu)menu.options.submenus[0]).moveCursorUp();
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.PlayMenu){
					menu.play.moveCursorUp();
				}
			}
		});
		settings.getKeyBindings().setAction("menuMoveDown", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
					menu.moveCursorDown();
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = (AudioMenu)menu.options.submenus[2];
						if(amenu.state == AudioMenu.AudioMenuState.Audio)
							amenu.moveCursorDown();
						else if(amenu.state == AudioMenu.AudioMenuState.AudioDevice)
							amenu.adMenu.moveCursorDown();
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Options)
						menu.options.moveCursorDown();
					else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Video)
						((VideoMenu)menu.options.submenus[1]).moveCursorDown();
					else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.KeyBindings)
						((KeyBindingsMenu)menu.options.submenus[0]).moveCursorDown();
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.PlayMenu){
					menu.play.moveCursorDown();
				}
			}
		});
		settings.getKeyBindings().setAction("menuMoveLeft", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = ((AudioMenu)menu.options.submenus[2]);
						if(amenu.getCurrentItem() <= 2)
							amenu.changeVolumeWithKeyboard(amenu.getCurrentItem(), 0);
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Video){
						VideoMenu vmenu = ((VideoMenu)menu.options.submenus[1]);
						if(vmenu.getCurrentItem() <= 2)
							vmenu.sliders[vmenu.getCurrentItem()].moveSliderLeft();;
					}
				}
			}
		});
		settings.getKeyBindings().setReleaseAction("menuMoveLeft", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = ((AudioMenu)menu.options.submenus[2]);
						amenu.stopVolumeChange();
					}
				}
			}
		});
		settings.getKeyBindings().setAction("menuMoveRight", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = ((AudioMenu)menu.options.submenus[2]);
						if(amenu.getCurrentItem() <= 2)
							amenu.changeVolumeWithKeyboard(amenu.getCurrentItem(), 1);
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Video){
						VideoMenu vmenu = ((VideoMenu)menu.options.submenus[1]);
						if(vmenu.getCurrentItem() <= 2)
							vmenu.sliders[vmenu.getCurrentItem()].moveSliderRight();;
					}
				}
			}
		});
		settings.getKeyBindings().setReleaseAction("menuMoveRight", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = ((AudioMenu)menu.options.submenus[2]);
						amenu.stopVolumeChange();
					}
				}
			}
		});
		settings.getKeyBindings().setAction("select", new Runnable(){
			public void run(){
				if(Arcana.gameState == State.Menu && menu.getState() == MenuState.MainMenu){
					menu.buttonPressed(menu.getCurrentItem());
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.OptionsMenu){
					if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Sound){
						AudioMenu amenu = ((AudioMenu)menu.options.submenus[2]);
						if(amenu.state == AudioMenu.AudioMenuState.Audio)
							amenu.buttonPressed(amenu.getCurrentItem());
						else if(amenu.state == AudioMenu.AudioMenuState.AudioDevice)
							amenu.adMenu.buttonPressed(amenu.adMenu.getCurrentItem());
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Options)
						menu.options.buttonPressed(menu.options.getCurrentItem());
					else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.Video){
						VideoMenu vmenu = ((VideoMenu)menu.options.submenus[1]);
						vmenu.buttonPressed(vmenu.getCurrentItem());
					}else if(menu.options.optionsState == OptionsMenu.OptionsMenuState.KeyBindings){
						KeyBindingsMenu kmenu = ((KeyBindingsMenu)menu.options.submenus[0]);
						kmenu.buttonPressed(kmenu.getCurrentItem());
					}
				}else if(Arcana.gameState == State.Menu && menu.getState() == MenuState.PlayMenu){
					menu.play.buttonPressed(menu.play.getCurrentItem());
				}
			}
		});
	
	}
	
	/**
	 * Changes the window title.
	 */
	public static void changeDisplayTitle(String title){
		frame.setTitle(title);
	}
	
	/**
	 * Starts the game.
	 * @param g The Game object to be started.
	 */
	public static void startGame(Game g){
		game = g;
		gameState = State.Game;
	}
	
	/**
	 * Returns an instance of the current game object.
	 */
	public static Game getGame(){
		return game;
	}

	public static void setNetworkManager(NetworkManager nm){
		netM = nm;
		if(netM instanceof Server)
			((Server)netM).host();
		else if(netM instanceof Client)
			((Client)netM).connect();
	}
	
	public static NetworkManager getNetworkManager(){
		return netM;
	}	
	
	/**
	 * Renders the game on screen.
	 */
	public void render(){
		try{
			if(getBufferStrategy() == null)	
				 createBufferStrategy(3);
			BufferStrategy bs = getBufferStrategy();
			Graphics g = bs.getDrawGraphics();
		
			//==============================
			//WHITE BACKGROUND
			g.setColor(Color.WHITE);
			g.fillRect(0, 0, frame.getContentPane().getWidth(), frame.getContentPane().getHeight());
			
			Graphics2D g2d = (Graphics2D) g;
			if(gameState == State.Menu){
				menu.render(g);
			}else if(gameState == State.Game){
				game.render(g);
			}else if(gameState == State.Pause){
				
			}
			if(settings.get("Frame", "showFPS").equals("1")){
				g.setColor(Color.BLACK);
				g.setFont(fpsFont);
				FontMetrics fm = g.getFontMetrics(fpsFont);
				FontRenderContext frc = fm.getFontRenderContext();
				GlyphVector gv = g2d.getFont().createGlyphVector(frc, Integer.toString(FPS));
				int height = (int) gv.getPixelBounds(frc, 0, fm.getHeight()).getHeight();
				g.drawString(Integer.toString(FPS), 0, height + 2);
			}
			//==============================
			
			g.dispose();
			bs.show();
		}catch(IllegalStateException | NullPointerException e){
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem: e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	/**
	 * Ticks the game logic.
	 */
	public void tick(){
		/*
		if(AudioLibrary.soundsPlaying.size() == 0){
			AudioLibrary.playSound(settings.getSounds().getMusic("Mi"));
		}
		*/
		try{
			if(gameState == State.Menu){
				menu.tick();
			}else if(gameState == State.Game){
				game.tick();
			}else if(gameState == State.Pause){
				
			}
		}catch(NullPointerException e){
			LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem: e.getStackTrace())
				LOGGER.println(elem.toString(), Logger.ERROR);
		}
	}
	
	public Menu getMenu(){
		return this.menu;
	}
	
	public static String getJarPath(){
		String path = Arcana.class.getProtectionDomain().getCodeSource().getLocation().getPath();
		String out = "";
		String[] folders = path.split("/");
		for(int i = 0; i < folders.length; i++){
			if(i != folders.length - 1)
				out += folders[i] + "/";
		}
		return out.substring(1).substring(0, out.substring(1).length() - 1).replace("%20", " ");
	}
	
	public static Settings getSettings(){
		return settings;
	}
	
	public static void exitGame(int errorCode){
		LOGGER.println("Saving settings");
		settings.saveSettings();
		LOGGER.println("Exiting game");
		running = false;
		System.exit(errorCode);
	}
	/**
	 * Method to restart the game
	 */
	public static void restartGame(){
		LOGGER.println("Saving settings");
		settings.saveSettings();
		LOGGER.println("Restarting game");
		try {
			Runtime.getRuntime().exec("cmd /c java -jar \"" + Arcana.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1) + "\" -Xms512m - Xmx1024m");
			running = false;
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
