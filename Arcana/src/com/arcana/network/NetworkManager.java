package com.arcana.network;

import java.net.InetAddress;

public interface NetworkManager {
	
	public void receivePacket(InetAddress source, byte[] data);
	
	public int getPort();
	
	public boolean isConnected();
	
	public long getPing();
	
	public int getPacketsReceived();
	
	public int getBytesReceived();
	
	public int getBytesSent();
	
	public void tick();
	
}
