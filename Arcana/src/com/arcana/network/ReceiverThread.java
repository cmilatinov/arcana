package com.arcana.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

import com.utils.src.NetworkUtils;

public class ReceiverThread extends Thread{
	
	private NetworkManager nm;
	private DatagramSocket server;
	private int port;
	private int buffer = 2048;
	private final int socketTimeout = 0;
	private boolean running = true;
	
	public ReceiverThread(NetworkManager nm){
		this.nm = nm;
		this.port = nm.getPort();
		this.start();
	}
	
	public void run(){
		while(running){
			try {
				server = new DatagramSocket(port);
				server.setReuseAddress(true);
				DatagramPacket p = NetworkUtils.receivePacket(server, socketTimeout, buffer);
				InetAddress source = p.getAddress();
				byte[] data = p.getData();
				if(data.length > p.getLength()){
					byte[] actualData = new byte[p.getLength()];
					for(int i = 0; i < p.getLength(); i++){
						actualData[i] = data[i];
					}
					data = actualData;
				}
				nm.receivePacket(source, data);
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void stopReceiver(){
		this.running = false;
		server.disconnect();
		server.close();
	}
}
