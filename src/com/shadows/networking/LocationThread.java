package com.shadows.networking;

import java.io.*;
import java.util.*;
import com.shadows.*;
import com.shadows.entity.specific.*;

public class LocationThread implements Runnable {
	public static List<String> playerNames = new ArrayList<String>();
	public static List<Projectile> projectiles = new ArrayList<Projectile>();
	public static Map<String, Player> playerMap = new HashMap<String, Player>();
	public static List<Projectile> projectilesToRemove = new ArrayList<Projectile>();
	
	@Override
	public void run() {
		while (Shadows.connected) {
			try {
				if (Shadows.isServer) {
					PlayerPacket loc = (PlayerPacket)UDPServer.lookForReceived();
					if (loc instanceof FirePlayerPacket) {
						launch((FirePlayerPacket)loc);
					} else {
						addPlayer(loc);
					}
				} else if (Shadows.connected) {
					PlayerPacket loc = (PlayerPacket)UDPClient.lookForReceived();
					if (loc instanceof FirePlayerPacket) {
						launch((FirePlayerPacket)loc);
					} else {
						addPlayer(loc);
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} 
			catch (ClassNotFoundException e) {
				System.err.println(e.toString());
			}
		}
	}
	
	private void launch(FirePlayerPacket loc) {
		float yRot = loc.getRotationY();
		float xRot = loc.getRotationX();
		
		MainPlayer.launchProj(Shadows.shootSpeed, xRot, yRot, loc.getX(), loc.getY(), loc.getZ(), loc.getProjName());
	}
	
	private void addPlayer(PlayerPacket loc) {
		String name = loc.getName();
		if (name != null) {
			if (true){//!name.contentEquals(Shadows.name)) {
				if (!playerNames.contains(loc.getName())) {
					playerNames.add(loc.getName());
				}
				playerMap.put(loc.getName(), new Player(loc.getX(), loc.getY(), loc.getZ(), 1, 1, 1, Shadows.model));
			}
		}
	}
}