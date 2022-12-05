package com.shadows.networking;

import java.io.*;

public class LevelSettingPacket implements Serializable{
	private static final long serialVersionUID = -8596121700238761587L;
	private long seed;
	private int rareness;
	
	public LevelSettingPacket(long seed, int rareness) {
		this.seed = seed;
		this.rareness = rareness;
	}

	public long getSeed() {
		return seed;
	}

	public int getRareness() {
		return rareness;
	}
}
