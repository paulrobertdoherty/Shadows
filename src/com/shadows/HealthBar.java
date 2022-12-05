package com.shadows;

import org.lwjgl.opengl.*;
import com.shadows.openGL.graphics.gui.*;
import static org.lwjgl.opengl.GL11.*;

public class HealthBar extends GUIObject {
	public static void draw() {
		float x = (float) (Shadows.health) / (float)(Shadows.maxHealth);
		if (x <= 0) {
			x = 0;
		}
		int halfDisplay = Display.getWidth() / 2;
		
		glPushMatrix();
		glScalef(halfDisplay, 16, 1);
		textureBox.renderStart(false);
		glColor4f(0.1f, 0.1f, 0.1f, 0.5f);
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		glPopMatrix();
		glPushMatrix();
		glScalef(halfDisplay * x, 16, 1);
		textureBox.renderStart(false);
		glColor4f(0, 0, 1, 1);
		textureBox.renderFinish(GL_TRIANGLES);
		glPopMatrix();
	}
}