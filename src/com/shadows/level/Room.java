package com.shadows.level;

import java.util.*;

public class Room {
	private Random random = null;
	
	public Room(Random random) {
		this.random = random;
	}

	public void generate() {
		int x = 0;
		while (x < 128) {
			int randomNum = random.nextInt(32) + 1;
			generateRooms(x, x + randomNum);
			x += randomNum;
		}
	}

	private void generateRooms(int x, int x2) {
		int y = 0;
		while (y < 128) {
			int randomNum = random.nextInt(32) + 1;
			generateRooms(x, x2, y, y + randomNum);
			y += randomNum;
		}
	}

	private void generateRooms(int x1, int x2, int z1, int z2) {
		for (int x = x1; x < x2; x++) {
			if ((x2 - x) % 3 != 0) {
				placeBlock(x, z1);
				placeBlock(x, z2);
			}
		}
		for (int z = z1; z < z2; z++) {
			if ((z2 - z) % 3 != 0) {
				placeBlock(x1, z);
				placeBlock(x2, z);
			}
		}
	}
	
	private void placeBlock(int x, int z) {
		if (x >= 128 || x < 0 || z >= 128 || z < 0) {
			return;
		}
		Level.block[x][z] = true;
	}
}
