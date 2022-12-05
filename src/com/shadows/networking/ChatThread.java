package com.shadows.networking;

import java.io.*;

import com.shadows.*;

public class ChatThread implements Runnable {
	public static String ip = "";
	public static int port = 0;
	
	@Override
	public void run() {
		if (Shadows.isServer) {
			try {
				TCPServer.tryConnectionWithClient(16);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				TCPClient.connectClient(ip, port);
			} catch (IOException e) {
				Chat.setConnectionStatus(e.toString());
			}
		}
		while (Shadows.connected) {
			if (Shadows.connected) {
				if (Shadows.isServer) {
					TCPServer.lookForReceived();
				} else {
					TCPClient.lookForReceived();
				}
			}
		}
	}
}