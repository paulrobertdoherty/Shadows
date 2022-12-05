package com.shadows.level;

import java.io.*;
import java.util.*;
import com.shadows.*;
import com.shadows.networking.*;

public class LevelSave implements Serializable {
	private static final long serialVersionUID = 3089554339436552193L;
	private float playerX, playerZ;
	private int collected, amountInLevel;
	private List<RobotSave> robotLocationList;
	private boolean[][] blockExists;

	public float getPlayerX() {
		return playerX;
	}

	public float getPlayerZ() {
		return playerZ;
	}

	public LevelSave(float playerX, float playerZ, int collected, int amountInLevel, List<RobotSave> robotLocationList, boolean[][] blockExists) {
		this.playerX = playerX;
		this.playerZ = playerZ;
		this.collected = collected;
		this.amountInLevel = amountInLevel;
		this.robotLocationList = robotLocationList;
		this.blockExists = blockExists;
	}
	
	public boolean[][] getBoxExistsArray() {
		return blockExists;
	}

	public List<RobotSave> getRobotLocationList() {
		return robotLocationList;
	}

	public void saveLevel(boolean quickSave) {
		byte[] data = null;
		try {
			data = UDPClient.serialize(this);
		} catch (IOException e) {
			System.err.println("Error creating bytes for save:");
			e.printStackTrace();
			return;
		}
		try {
			Shadows.setUpSaveFile(true, quickSave);
			Shadows.writer.write(data);
			Shadows.writer.flush();
			Shadows.writer = null;
		} catch (IOException e) {
			System.err.println("Error making save file.");
			e.printStackTrace();
			return;
		}
	}

	public int getAmount() {
		return amountInLevel;
	}
	
	public int getCollected() {
		return collected;
	}
}