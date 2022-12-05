package com.shadows.networking;

import java.util.*;
import java.util.List;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import com.shadows.*;
import com.shadows.openGL.graphics.*;
import com.shadows.openGL.graphics.gui.*;

public class Chat {
	private static String connectionStatus = "";
	private static boolean firstTimeCalled = true, selected = false;
	private static TextBox chat = null;
	private static List<String> chatList = new ArrayList<String>();
	
	public static void updateESC() {
		if (!Shadows.connected && firstTimeCalled) {
			KeyboardHandler.addBind("talk", Keyboard.KEY_RETURN);
			firstTimeCalled = false;
		}
		if (!selected) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				Shadows.pauseMenu = true;
				Mouse.setGrabbed(false);
			}
			selected = KeyboardHandler.getKey("talk");
		} else if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			selected = false;
			KeyboardHandler.isTyping = false;
		}
	}
	
	public static void drawChat() {
		if (Shadows.connected && firstTimeCalled) {
			chat = new TextBox(0, 0, Display.getWidth(), 16);
			KeyboardHandler.addBind("talk", Keyboard.KEY_RETURN);
			firstTimeCalled = false;
		}
		if (selected) {
			chat.setSelected(true);
			String message = chat.getText();
			if (message.length() != 0) {
				selected = false;
				String newMessage = Shadows.name + ": " + message;
				
				if (Shadows.isServer) {
					if (!message.contentEquals("start_game")) {
						if (Shadows.health > 0) {
							TCPServer.sendMessage(newMessage);
						}
						chatList.add(newMessage);
					} else if (message.contentEquals("start_game")) {
						Shadows.sendLevel();
					}
				}
				if (Shadows.connected && !Shadows.isServer) {
					if (Shadows.health > 0) {
						TCPClient.sendMessage(newMessage);
					} else {
						chatList.add(newMessage);
					}
				}
				if (!Shadows.connected) {
					chatList.add(newMessage);
				}
				
				chat.setText("");
				chat.setSelected(false);
			} else {
				chat.render(new Color(0f, 0f, 0f, 0.3f));
			}
		}
		
		drawChatList();
	}
	
	private static void drawChatList() {
		int size = chatList.size();
		int chatSize = (Display.getHeight() - 16) / 16;
		if (size > chatSize) {
			size = chatSize;
		}
		for (int i = 0; i < size; i++) {
			String s = chatList.get(i);
			Text.drawText(0, ((size - i) + 1) * 16, 16, 16, s, new Color(1, 1, 1, 1));
		}
	}

	public static void sendMessage(String player, String message) {
		TCPClient.sendMessage(player + ": " + message);
	}

	public static void setConnectionStatus(String status) {
		connectionStatus = status;
	}
	
	public static String getCurrentConnectionStatus() {
		return connectionStatus;
	}

	public static void sendMessageToChatWindow(String string) {
		chatList.add(string);
	}
}
