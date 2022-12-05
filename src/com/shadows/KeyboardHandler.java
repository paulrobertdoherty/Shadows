package com.shadows;

import java.util.*;
import org.lwjgl.input.*;

public class KeyboardHandler {
	private static Map<Integer, HashMap<String, Boolean>> keyBinding = new HashMap<Integer, HashMap<String, Boolean>>();
	private static Map<Integer, String> keyBinding2 = new HashMap<Integer, String>();
	private static Map<String, Integer> keyBinding3 = new HashMap<String, Integer>();
	private static StringBuilder currentText = new StringBuilder();
	public static boolean isTyping = false;
	private static boolean holdingShift = false;
	
	public static void update() {
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				char currentTypingKey = Keyboard.getEventCharacter();
				if (isTyping) {
					if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
						holdingShift = true;
						continue;
					}
					if (holdingShift) {
						currentTypingKey = Character.toUpperCase(currentTypingKey);
					}
					if (Keyboard.getEventKey() != Keyboard.KEY_BACK) {
						currentText.append(currentTypingKey);
					} else if (currentText.length() > 0) {
						currentText.setLength(currentText.length() - 1);
					}
				} else {
					int currentKey = Keyboard.getEventKey();
					if (keyBinding.get(currentKey) != null && keyBinding2.get(currentKey) != null) {
						if (Mouse.isGrabbed()) {
							keyBinding.get(currentKey).put(keyBinding2.get(currentKey), new Boolean(true));
						}
					}
				}
			} else {
				int currentKey = Keyboard.getEventKey();
				if (keyBinding.get(currentKey) != null && keyBinding2.get(currentKey) != null) {
					keyBinding.get(currentKey).put(keyBinding2.get(currentKey), new Boolean(false));
				}
				if (Keyboard.getEventKey() == Keyboard.KEY_LSHIFT || Keyboard.getEventKey() == Keyboard.KEY_RSHIFT) {
					holdingShift = false;
				}
			}
		}
	}
	
	public static String getText() {
		return currentText.toString();
	}
	
	public static void setText(String text) {
		currentText = new StringBuilder(text);
	}
	
	public static void addBind(String bindCode, int key) {
		if (keyBinding.get(bindCode) == null) {
			keyBinding2.put(key, bindCode);
			keyBinding3.put(bindCode, key);
			HashMap<String, Boolean> hashMap = new HashMap<String, Boolean>();
			hashMap.put(bindCode, new Boolean(false));
			keyBinding.put(key, hashMap);
		}
	}
	
	public static boolean getKey(String bindCode) {
		return keyBinding.get(keyBinding3.get(bindCode)).get(bindCode);
	}
}