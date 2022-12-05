package com.shadows.networking;

import java.io.*;

public class PlayerPacket implements Serializable {
	private static final long serialVersionUID = -56296039461770576L;
	private float x, y, z;
	private String name;
	
	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getZ() {
		return z;
	}

	public String getName() {
		return name;
	}

	public PlayerPacket(float x, float y, float z, String name) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.name = name;
	}
}