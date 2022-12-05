package com.shadows.networking;

import java.io.*;

public class DiedPacket implements Serializable{
	private static final long serialVersionUID = 7714815985453679349L;
	private String name = null, hitterName = null;
	private int blocks = 0;
	
	public DiedPacket(String name, String hitterName, int blocks) {
		this.name = name;
		this.hitterName = hitterName;
		this.blocks = blocks;
	}

	public String getHitterName() {
		return hitterName;
	}

	public int getBlocks() {
		return blocks;
	}

	public String getName() {
		return name;
	}
}