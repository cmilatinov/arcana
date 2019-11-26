package com.arcana.network;


import java.net.InetAddress;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.HashMap;
import java.util.Set;

import com.arcana.characters.Sorlo;
import com.arcana.entities.Entity;
import com.arcana.entities.EntityFireball;
import com.arcana.game.Game;
import com.arcana.game.TestMap;
import com.arcana.src.Arcana;
import com.arcana.utils.Commands;
import com.arcana.utils.Maths;
import com.utils.src.NetworkUtils;

public class Server extends Thread implements NetworkManager{
	
	public static final int SERVER_PORT = 43356;
	
	ReceiverThread rt;
	private volatile boolean isConnected;
	private volatile InetAddress client;
	private long ping = 0;
	private int bytesReceived = 0;
	private int bytesSent = 0;
	private String username;
	private volatile String clientUsername;
	private int clientTimeout = 3000;
	private int packetsPerSec = 0;
	private volatile long lastInvalidPacket = 0;
	private Game game;
	private Commands cmd;
	private NumberFormat nf = Maths.getNetworkNumberFormat();
	
	public Server(String username){
		this.username = username;
		Arcana.changeDisplayTitle(Arcana.TITLE + " v" + Arcana.VERSION + " - Server");
	}
	
	public void host(){
		InetAddress[] ips = NetworkUtils.getActiveAddresses();
		Arcana.LOGGER.println("Hosting server on the following IPs: ");
		for(InetAddress ip : ips)
			Arcana.LOGGER.println(ip.getHostAddress());
		rt = new ReceiverThread(this);
	}
	
	public void tick(){
		long currentTime = System.currentTimeMillis();
		if(isConnected && currentTime - lastInvalidPacket > clientTimeout && lastInvalidPacket != Long.parseLong("0")){
			isConnected = false;
			lastInvalidPacket = 0;
			Arcana.LOGGER.println(clientUsername + " @ " + client.getHostAddress() + " disconnected from server.");
			client = null;
		}else if(isConnected && game != null){
			bytesSent += new GamePacket(GamePacket.PACKET_GAME_UPDATE, getUpdateString()).sendPacket(client, Client.CLIENT_PORT);
		}
	}
	
	private String getUpdateString(){
		String result = "pX:" + nf.format(game.getPlayer().getPosition().getX()) + ";"
				+ "pY:" + nf.format(game.getPlayer().getPosition().getY()) + ";"
				+ "pvX:" + nf.format(game.getPlayer().getVelocity().getX()) + ";"
				+ "pvY:" + nf.format(game.getPlayer().getVelocity().getY()) + ";"
				+ "pisF:" + game.getPlayer().isFalling() + ";"
				+ "anim:" + game.getPlayer().getCharacter().currentAnimationName() + ";"
				+ "animF:" + game.getPlayer().getCharacter().currentAnimationFrame() + ";"
				+ "oX:" + nf.format(game.getOpponent().getPosition().getX()) + ";"
				+ "oY:" + nf.format(game.getOpponent().getPosition().getY()) + ";"
				+ "ovX:" + nf.format(game.getOpponent().getVelocity().getX()) + ";"
				+ "ovY:" + nf.format(game.getOpponent().getVelocity().getY()) + ";"
				+ "oisF:" + game.getOpponent().isFalling() + ";";
				
		Set<String> names = Arcana.getGame().getController().getAllEntities().keySet();
		HashMap<String, Entity> entities = Arcana.getGame().getController().getAllEntities();
		for(String name : names){
			Entity e = entities.get(name);
			if(e instanceof EntityFireball){
				EntityFireball f = (EntityFireball) e;
				result += "e|" + name + ":fireball," + nf.format(f.getPosition().getX()) + ","
						+ nf.format(f.getPosition().getY()) + ","  
						+ nf.format(f.getVelocity().getX()) + ","
						+ nf.format(f.getVelocity().getY()) + ";";
			}
		}
		return result;
	}
	
	private void startGame(){
		game = new Game(Arcana.getSettings(), Arcana.frame, new Sorlo(), new Sorlo(), new TestMap(), this);
		Arcana.startGame(game);
		this.cmd = new Commands(game, this);
	}
	
	public int getBytesReceived(){
		int b = bytesReceived;
		bytesReceived = 0;
		return b;
	}
	
	public int getBytesSent(){
		int b = bytesSent;
		bytesSent = 0;
		return b;
	}
	
	public long getPing(){
		Long p = ping;
		ping = 0;
		return p;
	}
	
	public int getPacketsReceived(){
		int p = packetsPerSec;
		packetsPerSec = 0;
		return p;
	}
	
	public void stopServer(){
		try {
			rt.join();
			this.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void receivePacket(InetAddress source, byte[] data) {
		bytesReceived += data.length;
		GamePacket packet = GamePacket.parsePacket(new String(data));
		if(packet.getType() == GamePacket.PACKET_CONNECT && !isConnected){
			try{
				clientUsername = packet.getValue("user");
				if(Double.parseDouble(packet.getValue("version")) != Arcana.VERSION){
					new GamePacket(GamePacket.PACKET_DENY_CONNECTION, "reason:Invalid Version").sendPacket(source, Client.CLIENT_PORT);;
				}else{
					client = source;
					isConnected = true;
					getPing(packet);
					Arcana.LOGGER.println(clientUsername + " successfully connected from " + source.getHostAddress() + ".");
					bytesSent += new GamePacket(GamePacket.PACKET_ACCEPT_CONNECTION, "user:" + username).sendPacket(source, Client.CLIENT_PORT);;
					this.startGame();
				}
			}catch(NullPointerException e){
				e.printStackTrace();
			}
		}else if(packet.getType() == GamePacket.PACKET_INVALID && isConnected && source.equals(client)){
			getPing(packet);
			bytesSent += new GamePacket(GamePacket.PACKET_INVALID, "").sendPacket(client, Client.CLIENT_PORT);
		}else if(packet.getType() == GamePacket.PACKET_GAME_UPDATE && isConnected && source.equals(client)){
			getPing(packet);
			game.update(packet, this);
		}else if(packet.getType() == GamePacket.PACKET_COMMAND && isConnected && source.equals(client)){
			getPing(packet);
			String[] cmds = packet.getValue("c").split(",");
			if(cmd != null){
				for(String c: cmds)
					cmd.executeCommand(c);
			}
		}
	}
	
	private void getPing(GamePacket p){
		packetsPerSec++;
		Long time = Long.parseLong(p.getValue("time"));
		lastInvalidPacket = System.currentTimeMillis();
		ping = Timestamp.from(Instant.now()).getTime() - time;
	}
	
	public boolean isConnected(){
		return isConnected;
	}

	public int getPort() {
		return SERVER_PORT;
	}
}
