package com.shadows.networking;

import java.io.*;

public class WonPacket implements Serializable {
	private static final long serialVersionUID = 7239421696681740650L;
	private String message = null;

	public WonPacket(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}