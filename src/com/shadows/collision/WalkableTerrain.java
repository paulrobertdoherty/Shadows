package com.shadows.collision;

import org.lwjgl.util.vector.*;

public class WalkableTerrain {
	private Vector3f[][] terrainNormals, terrainNormals2;
	private float[][] terrainPoints;
	private float size;
	private int tiles;
	
	/**
	 * Creates terrain collision from a terrain heightmap.
	 * @param terrainPoints
	 * @param tiles
	 * @param size
	 */
	public WalkableTerrain(float[][] terrainPoints, int tiles, float size) {
		this.terrainPoints = terrainPoints;
		this.size = size;
		this.tiles = tiles;
		addNormals();
	}

	private void addNormals() {
		terrainNormals = new Vector3f[terrainPoints.length][terrainPoints.length];
		terrainNormals2 = new Vector3f[terrainPoints.length][terrainPoints.length];
		
		for (int i = 0; i < tiles; i++) {
			for (int j = 0; j < tiles; j++) {
				if (i + 1 > terrainPoints.length) {
					terrainNormals[i][j] = new Vector3f(0, 0, terrainPoints[i + 1][j + 1] - terrainPoints[i][j]);
					terrainNormals2[i][j] = new Vector3f(0, 0, terrainPoints[i][j + 1] - terrainPoints[i][j]);
				} else if (j + 1 > terrainPoints.length) {
					terrainNormals[i][j] = new Vector3f(terrainPoints[i + 1][j] - terrainPoints[i][j], 0, 0);
					terrainNormals2[i][j] = new Vector3f(terrainPoints[i + 1][j + 1] - terrainPoints[i][j], 0, 0);
				} else {
					terrainNormals[i][j] = new Vector3f(terrainPoints[i + 1][j] - terrainPoints[i][j], 0, terrainPoints[i + 1][j + 1] - terrainPoints[i + 1][j]);
					terrainNormals2[i][j] = new Vector3f(terrainPoints[i + 1][j + 1] - terrainPoints[i][j + 1], 0, terrainPoints[i][j + 1] - terrainPoints[i][j]);
				}
			}
		}
	}
	
	/**
	 * A method for getting the proper Y position for the player entity corresponding to the correct tile.
	 * @param x
	 * @param y
	 * @param xLeft
	 * @param yLeft
	 * @return Proper Y position.
	 */
	public float getYPositionOnTile(int x, int y, float xLeft, float yLeft) {
		if (canWalk(x + xLeft, y + yLeft)) {
			/*
			float tileSize = size / (tiles + 1);
			Vector3f orig = new Vector3f(x * tileSize, terrainPoints[(int)(x * tileSize)][(int)(y * tileSize)], y * tileSize);
			Vector3f norm = terrainNormals[(int)(x * tileSize)][(int)(y * tileSize)];
			Vector3f norm2 = terrainNormals2[(int)(x * tileSize)][(int)(y * tileSize)];
			
			float yx = 0;
			float yz = 0;
			if (xLeft * tileSize <= yLeft * tileSize) {
				yx = norm.x * (xLeft * tileSize);
				yz = norm.z * (yLeft * tileSize);
			} else {
				yx = norm2.x * (xLeft * tileSize);
				yz = norm2.z * (yLeft * tileSize);
			}
			*/
			
			return 0;
		}
		return -1;
	}
	
	public boolean canWalk(Cube c) {
		if (canWalk(c.getX(), c.getZ())) {
			float diffY = c.getY();
			float diffY2 = c.getY() + c.getHeight();
			
			float cubeDiffX = c.getX() - (int)c.getX();
			float cubeDiffZ = c.getZ() - (int)c.getZ();
			float cubeDiffX2 = (c.getX() + c.getLength()) - (int)(c.getX() + c.getLength());
			float cubeDiffZ2 = (c.getZ() + c.getWidth()) - (int)(c.getZ() + c.getWidth());
			
			float yPos = getYPositionOnTile((int)c.getX(), (int)c.getZ(), cubeDiffX, cubeDiffZ);
			float yPos2 = getYPositionOnTile((int)(c.getX() + c.getLength()), (int)(c.getZ() + c.getWidth()), cubeDiffX2, cubeDiffZ2);
			
			if ((diffY <= yPos && 
					diffY2 >= yPos)
				&& (diffY <= yPos2 && diffY2 >= yPos2)) {
				return true;
			}
		}
		return false;
	}

	private boolean canWalk(float x, float z) {
		return x > 0 && z > 0 && x < size && z < size;
	}
}