package com.arcana.network;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.arcana.src.Arcana;
import com.utils.src.Logger;
import com.utils.src.NetworkUtils;

public class GamePacket {
	
	public static final int PACKET_INVALID = 0;
	public static final int PACKET_CONNECT = 1;
	public static final int PACKET_ACCEPT_CONNECTION = 2;
	public static final int PACKET_DENY_CONNECTION = 3;
	public static final int PACKET_GAME_UPDATE = 4;
	public static final int PACKET_COMMAND = 5;
	private int type;
	private String msg;
	private String[] vars;
	private String[] values;
	
	public GamePacket(int type, String msg){
		this.type = type;
		this.msg = "time:" + Timestamp.from(Instant.now()).getTime() + ";" + msg;
		String[] strs = this.msg.split(";");
		String[] vars = new String[strs.length]; 
		String[] values = new String[strs.length]; 
		try{
			for(int i = 0; i < strs.length; i++){
				vars[i] = strs[i].split(":")[0];
				values[i] = strs[i].split(":")[1];
			}
		}catch(ArrayIndexOutOfBoundsException e){
			e.printStackTrace();
		}
		this.vars = vars;
		this.values = values;
	}
	
	public GamePacket(int type, String[] vars, String[] values){
		this.type = type;
		this.vars =  new String[vars.length + 1];
		this.values =  new String[values.length + 1];
		this.vars[0] = "time";
		this.values[0] = Long.toString(Timestamp.from(Instant.now()).getTime());
		String m = "";
		for(int i = 0; i < this.vars.length; i++){
			if(i == 0){
				m += this.vars[i] + ":" + this.values[i] + ";";
			}else{
				m += vars[i] + ":" + values[i] + ";";
				this.vars[i] = vars[i - 1];
				this.values[i] = vars[i - 1];
			}
		}
		this.msg = m;
	}
	
	public String getValue(String var){
		for(int j = 0; j < vars.length; j++){
			if(vars[j].equals(var))
				return values[j];
		}
		return null;
	}
	
	public List<String> getVariableNamesStartingWith(String s){
		List<String> result = new ArrayList<String>();
		for(String str: vars){
			if(str.startsWith(s))
				result.add(str);
		}
		return result;
	}
	
	public String toString(){
		String integ = Integer.toString(type);
		while(integ.length() < 3)
			integ = "0" + integ;
		return integ + "/" + msg;
	}
	
	public int getType(){
		return type;
	}
	
	public String getMsg(){
		return msg;
	}
	
	public int sendPacket(InetAddress ip, int port){
		try{
			byte[] data = this.toString().getBytes();
			NetworkUtils.sendPacket(data, ip.getHostAddress(), port);
			return data.length;
		}catch(IOException | NullPointerException e){
			Arcana.LOGGER.println(e.toString(), Logger.ERROR);
			for(StackTraceElement elem : e.getStackTrace())
				Arcana.LOGGER.println(elem.toString(), Logger.ERROR);
			return 0;
		}
	}
	
	public static GamePacket parsePacket(String data){
		String integ = data.split("/")[0];
		String msg = "";
		if(data.split("/").length > 1)
			msg = data.split("/")[1];
		return new GamePacket(Integer.parseInt(integ), msg);
	}
	
	public long timestamp(){
		return Long.parseLong(this.getValue("time"));
	}
}
