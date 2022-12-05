package com.shadows;

import org.lwjgl.*;
import org.lwjgl.openal.*;
import org.lwjgl.opengl.*;

public class Screen {
	private int width, height;
	public static boolean vSyncEnabled = false, canRender = false;
	
	/**
	 * The constructor for the window.
	 * Parameters are used if the LWJGL does 
	 * not support the display
	 * @param width
	 * @param height
	 */
	public Screen(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Makes the window work
	 */
	public void createWindow() {
		try {
			produceWindow();
		} catch (LWJGLException e) {
			System.err.println("Your display does not support this game");
			System.exit(1);
		}
		try {
			AL.create();
		} catch (LWJGLException e) {
			System.err.println("OpenAL not supported on your machine.");
		}
		
		vSyncEnabled = true;
	}
	
	/**
	 * Actually creates the window
	 */
	public void produceWindow() throws LWJGLException {
		Display.setDisplayMode(new DisplayMode(this.width, this.height));
		Display.setVSyncEnabled(true);
		Display.setTitle("An Exploration of Pointlessness");
		Display.create();
	}
}
