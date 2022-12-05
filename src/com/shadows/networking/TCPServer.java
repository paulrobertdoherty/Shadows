package com.shadows.networking;

import java.io.*;
import java.net.*;

import com.shadows.*;
import com.shadows.entity.specific.MainPlayer;

public class TCPServer {
	private static Socket connection = null;
	private static ServerSocket socket = null;
	private static ObjectOutputStream output = null;
	public static int port = 0;
	public static ObjectInputStream input = null;
	public static FileLoader loader = null;
	
	public static void tryConnectionWithClient(int availableConnections) throws IOException {
		if (port == 0) {
			getPort();
		}
		socket = new ServerSocket(port, availableConnections);
		Chat.setConnectionStatus("Waiting for connection...");
		connection = socket.accept();
		Chat.setConnectionStatus("Connected to client!  Setting up streams.");
		
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
			Chat.sendMessageToChatWindow("Error sending message.");
		}
	}
	
	public static void lookForReceived() {
		try {
			Object object = input.readObject();
			if (object instanceof String) {
				String message = (String)(object);
				Chat.sendMessageToChatWindow(message);
				sendMessage(message);
			} else if (object instanceof DiedPacket) {
				DiedPacket p = (DiedPacket)object;
				doStuff(p);
				sendMessage(p);
			} else if (object instanceof WonPacket) {
				WonPacket w = (WonPacket)object;
				String name = w.getMessage().split(":::::")[0];
				String message = w.getMessage().split(":::::")[1];
				CenterText.setMessage(name + " says \"" + message + "\"", false);
				sendMessage(w);
			}
		} catch (EOFException e) {
			Chat.sendMessageToChatWindow("Someone has left the game.");	
		} catch (ClassNotFoundException e) {
			Chat.sendMessageToChatWindow("Unknown packet sent to server.");
		} catch (IOException e) {
			Chat.sendMessageToChatWindow("Something happened called a " + e + ".");
		}
	}
	
	public static void close() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void doStuff(DiedPacket p) {
		Chat.sendMessageToChatWindow(p.getName() + " has had its blocks stolen.");
		LocationThread.playerMap.remove(p.getName());
		LocationThread.playerNames.remove(p.getName());
		String hitName = p.getHitterName();
		if (hitName.contentEquals(Shadows.name)) {
			MainPlayer.boxesCollected += p.getBlocks();
		}
		MainPlayer.hitSound.play();
	}

	public static void getPort() throws FileNotFoundException, IOException {
		loader = new FileLoader("/res/server_properties.txt", false);
		port = Integer.parseInt(loader.getProperty("tcpPort"));
	}
}