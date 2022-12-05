package com.shadows.level;

import java.util.*;
import com.shadows.*;
import com.shadows.entity.specific.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.*;

public class Level {
	private int size = 0, rarenessLevel = 0;
	public static Random random = null;
	public static boolean[][] block = null;
	public boolean placedPlayer = false;
	public static List<Wall> walls = new ArrayList<Wall>();
	public static List<Box> boxes = new ArrayList<Box>();
	public static List<Button> buttons = new ArrayList<Button>();
	public static Wall[][] wallArray = null;
	public static Box[][] boxArray = null;
	public static Button[][][] buttonArray = null;
	public static int totalBoxCount = 0;
	
	public Level(float size, int rarenessLevel) {
		this(size, System.currentTimeMillis(), rarenessLevel);
	}

	public Level(float size, long seed, int rarenessLevel) {
		this.size = (int) size;
		this.rarenessLevel = rarenessLevel;
		random = new Random(seed);
		if (block == null) {
			block = new boolean[this.size][this.size];
		}
		if (wallArray == null) {
			wallArray = new Wall[this.size][this.size];
		}
		if (boxArray == null) {
			boxArray = new Box[this.size][this.size];
		}
		totalBoxCount = 0;
		
		generateValuesAndPopulate();
	}

	public static void generateTerrain(int size) {
		Shadows.floor = new Floor(size / 2, size / 2, size / 2, 0, size / 2, 128);
		Shadows.floor.draw();
	}

	private void generateValuesAndPopulate() {
		Room r = new Room(random);
		r.generate();
		
		for (int x = 0; x < size; x++) {
			for (int z = 1; z < size; z++) {
				boolean bool = false;
				if (x > 1) {
					bool = block[x][z];
				}
				populate(x, z, bool);
			}
		}
	}

	private void populate(int x, int z, boolean block) {
		if (block && (z <= 1 || !(wallArray[x][z - 2] != null && Level.wallArray[x][z - 1] == null)) && z <= 126) {
			Wall wall = new Wall(x, 0, z, 1, 2f, 1, Shadows.box);
			walls.add(wall);
			wallArray[x][z] = wall;
		} else {
			if (random.nextInt(rarenessLevel) == 0 && ((z < 2) || (!Level.block[x][z - 1] && !Level.block[x][z - 2]))) {
				if (boxArray[x][z] == null) {
					Box entityBox = new Box(x, 0, z, 1, 1, 1, Shadows.box, new Color(1, 0, 1, 1));
					entityBox.setExists(true);
					boxes.add(entityBox);
					boxArray[x][z] = entityBox;
				} else {
					boxArray[x][z].setExists(true);
					boxes.add(boxArray[x][z]);
				}
				
				totalBoxCount++;
			} else if (!placedPlayer && random.nextInt(rarenessLevel / 2) == 0) {
				Shadows.cameraLocation.x = 1;
				Shadows.cameraLocation.z = 1;
				
				Shadows.player.setX(Shadows.cameraLocation.x);
				Shadows.player.setZ(Shadows.cameraLocation.z);
				
			//	Shadows.player.setCollision(new Cube(Shadows.cameraLocation.x, Shadows.cameraLocation.y, 
			//	Shadows.cameraLocation.z, 1, 1, 1));
				
				placedPlayer = true;
			}
		}
	}
	
	public static void clearLevel() {
		boxArray = null;
		wallArray = null;
		walls.clear();
		boxes.clear();
	}
	
	public static int undoRemovedBlocks(int playerX, int playerZ) {
		int removedBlocks = 0;
		
		for (int x = playerX - 16; x < playerX + 16; x++) {
			if (x < 0 || x > 127) {
				continue;
			}
			for (int z = playerZ - 16; z < playerZ + 16; z++) {
				if (z < 0 || z > 127) {
					continue;
				}
				
				if (boxArray[x][z] != null) {
					if (!boxArray[x][z].exists()) {
						boxArray[x][z].setExists(true);
						boxes.add(boxArray[x][z]);
						removedBlocks++;
						totalBoxCount++;
					}
				}
			}
		}
		
		return removedBlocks;
	}
}