package com.shadows.networking;

import java.io.*;
import java.net.*;
import com.shadows.*;

public class TCPClient {
	private static Socket connection = null;
	private static ObjectOutputStream output = null;
	public static ObjectInputStream input = null;
	
	public static void connectClient(String ip, int port) throws IOException {
		Chat.setConnectionStatus("Connecting to server...");
		connection = new Socket(InetAddress.getByName(ip), port);
		Chat.setConnectionStatus("Connected to server!  Setting up streams.");
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		Chat.setConnectionStatus("Streams are set up!");
		Shadows.connected = true;
	}
	
	public static void sendMessage(Object message) {
		try {
			output.writeObject(message);
			output.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Chat.sendMessageToChatWindow("Error sending message.");
		}
	}
	
	public static void lookForReceived() {
		try {
			Object object = input.readObject();
			if (object instanceof String) {
				Chat.sendMessageToChatWindow((String)(object));
			} else if (object instanceof LevelSettingPacket) {
				LevelSettingPacket o = (LevelSettingPacket)(object);
				Shadows.setLevel(o.getSeed(), o.getRareness());
			} else if (object instanceof DiedPacket) {
				DiedPacket p = (DiedPacket)object;
				TCPServer.doStuff(p);
			} else if (object instanceof WonPacket) {
				WonPacket w = (WonPacket)object;
				String name = w.getMessage().split(":::::")[0];
				String message = w.getMessage().split(":::::")[1];
				if (!name.contentEquals(Shadows.name)) {
					CenterText.setMessage(name + " says \"" + message + "\"", false);
				}
			}
		} catch (IOException e) {
			if (e instanceof EOFException) {
				Shadows.connected = false;
			} else {
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Closes the streams.
	 */
	public static void close() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}