package com.shadows.openGL.graphics.gui;

import com.shadows.openGL.drawer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

public abstract class GUIObject {
	public static TextureDrawer textureBox = null;
	
	public static void draw() {
		if (textureBox == null) {
			textureBox = new TextureDrawer();
		}
		renderCube();
	}

	private static void renderCube() {
		textureBox.begin(2);
		
		textureBox.addUV(0, 0);
		textureBox.addVertex(0, 1, 0);
		
		textureBox.addUV(1, 0);
		textureBox.addVertex(1, 1, 0);
		
		textureBox.addUV(1, 1);
		textureBox.addVertex(1, 0, 0);
		
		textureBox.addUV(1, 1);
		textureBox.addVertex(1, 0, 0);
		
		textureBox.addUV(0, 1);
		textureBox.addVertex(0, 0, 0);
		
		textureBox.addUV(0, 0);
		textureBox.addVertex(0, 1, 0);
		
		textureBox.end(GL_STATIC_DRAW);
	}
	
	public static void ready2D() {
		glDisable(GL_LIGHTING);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_FRONT);
		glUseProgram(0);
	}
	
	public static void ready3D() {
		glEnable(GL_LIGHTING);
		glCullFace(GL_BACK);
		glDisable(GL_CULL_FACE);
	}
}