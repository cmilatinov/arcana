package com.arcana.network;

import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;

import com.arcana.characters.Sorlo;
import com.arcana.game.Game;
import com.arcana.game.TestMap;
import com.arcana.src.Arcana;
import com.arcana.utils.Commands;

public class Client extends Thread implements NetworkManager{
	
	public static final int CLIENT_PORT = 43355;
	
	ReceiverThread rt;
	private boolean isConnected;
	private InetAddress serverIP;
	private int clientTimeout = 3000;
	private int packetsPerSec = 0;
	private long ping = 0;
	private int bytesReceived = 0;
	private int bytesSent = 0;
	private long lastInvalidPacket;
	private String username;
	private volatile String serverUsername;
	private Game game;
	private String nextCommand = "";
	private Commands cmd;
	
	
	public Client(InetAddress serverIP, String username){
		this.serverIP = serverIP;
		this.username = username;
		Arcana.changeDisplayTitle(Arcana.TITLE + " v" + Arcana.VERSION + " - Client");
	}
	
	public void connect(){
		rt = new ReceiverThread(this);
	}
	
	public void tick(){
		long currentTime = System.currentTimeMillis();
		if(lastInvalidPacket != 0 && currentTime - lastInvalidPacket > clientTimeout && isConnected){
			isConnected = false;
			Arcana.LOGGER.println("Lost connection to server");
			lastInvalidPacket = 0;
		}	
		if(isConnected){
			if(game != null){
				bytesSent += new GamePacket(GamePacket.PACKET_COMMAND, 
						nextCommand.equals("") ? "c:none;" : "c:" + nextCommand + ";").sendPacket(serverIP, Server.SERVER_PORT);
				nextCommand = "";
			}else
				bytesSent += new GamePacket(GamePacket.PACKET_INVALID, "").sendPacket(serverIP, Server.SERVER_PORT);
		}else{
			bytesSent += new GamePacket(GamePacket.PACKET_CONNECT, "user:" + username + ";version:" + Arcana.VERSION).sendPacket(serverIP, Server.SERVER_PORT);
		} 
		
	}
	
	private void startGame(){
		game = new Game(Arcana.getSettings(), Arcana.frame, new Sorlo(), new Sorlo(), new TestMap(), this);
		Arcana.startGame(game);
		this.cmd = new Commands(game, this);
	}
	
	public long getPing(){
		Long p = ping;
		ping = 0;
		return p;
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
	
	public int getPacketsReceived(){
		int p = packetsPerSec;
		packetsPerSec = 0;
		return p;
	}
	
	public void jump(){
		nextCommand += "jump,";
	}
	
	public void walkLeft(){
		nextCommand += "moveLeft,";
	}
	
	public void walkRight(){
		nextCommand += "moveRight,";
	}
	
	public void stopMoving(){
		nextCommand += "stopMovement,";
	}
	
	public void ability1(String args){
		nextCommand += "ability1(" + args + "),";
	}
	
	public void receivePacket(InetAddress source, byte[] data) {
		bytesReceived += data.length;
		GamePacket packet = GamePacket.parsePacket(new String(data));
		if(packet.getType() == GamePacket.PACKET_ACCEPT_CONNECTION && source.equals(serverIP)){
			isConnected = true;
			serverUsername = packet.getValue("user");
			getPing(packet);
			startGame();
			Arcana.LOGGER.println("Connected to " + serverUsername + "'s server at " + source.getHostAddress());
		}else if(packet.getType() == GamePacket.PACKET_DENY_CONNECTION && source.equals(serverIP)){
			isConnected = false;
			Arcana.LOGGER.println("Failed to connect due to the following reason : " + packet.getValue("reason"));
			disconnect();
		}else if(packet.getType() == GamePacket.PACKET_INVALID && isConnected && source.equals(serverIP)){
			getPing(packet);
		}else if(packet.getType() == GamePacket.PACKET_GAME_UPDATE && isConnected && source.equals(serverIP)){
			getPing(packet);
			game.update(packet, this);
		}else if(packet.getType() == GamePacket.PACKET_COMMAND && isConnected && source.equals(serverIP)){
			getPing(packet);
			String[] cmds = packet.getValue("c").split(",");
			if(cmd != null){
				for(String c: cmds)
					cmd.executeCommand(c);
			}
		}
	}
	
	public void disconnect(){
		rt.stopReceiver();
		Arcana.setNetworkManager(null);
		Arcana.changeDisplayTitle(Arcana.TITLE  + " v" + Arcana.VERSION);
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
		return CLIENT_PORT;
	}
}
