package com.shadows.openGL;

import static org.lwjgl.opengl.GL11.*;

public class LightHandler {
	public static void setUp() {
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);
		glEnable(GL_NORMALIZE);
		glEnable(GL_COLOR_MATERIAL);
	}
}
