package com.shadows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.*;
import com.shadows.openGL.graphics.*;
import com.shadows.openGL.graphics.gui.*;

public class Crosshair extends GUIObject {
	private static boolean firstDrawn = true;
	private static Texture texture = null;

	public static void draw() {
		if (firstDrawn) {
			texture = new Texture("/res/textures/cross.png");
		}
		texture.bind();
		glPushMatrix();
		glTranslatef((Display.getWidth() / 2) - 8, (Display.getHeight() / 2) - 8, 0);
		glScalef(16, 16, 1);
		textureBox.renderStart();
		glColor4f(1, 1, 1, 1);
		textureBox.renderFinish(GL_TRIANGLES);
		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
	}
}