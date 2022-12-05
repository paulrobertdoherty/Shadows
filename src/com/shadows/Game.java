package com.shadows;

public class Game {
	private long lastFrame;
	private long lastFPS;
	public int delta;
	private int fps;
	
	public long getTime() {
		return System.currentTimeMillis();
	}
	
	public int getDelta() {
		long time = getTime();
		delta = (int) (time - lastFrame);
		lastFrame = getTime();
		if (delta == 0) {
			delta = 1;
		}
		return delta;
	}
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Shadows.fps = fps + " FPS";
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}

	public void start() {
		lastFPS = getTime();
	}
}