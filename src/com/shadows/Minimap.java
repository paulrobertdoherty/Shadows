package com.shadows;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.*;
import com.shadows.openGL.graphics.gui.*;

public class Minimap extends GUIObject {
	public static void draw() {
		float rot = Shadows.cameraRotation.y;
		if (rot < 0) {
			rot += 360;
		} else if (rot > 360) {
			rot -= 360;
		}
		//variables representing numbers
		int v128 = (int)(128 * ((float)Display.getWidth() / 640.0f));
		
		float x = Display.getWidth() - v128;
		glPushMatrix();
		glTranslatef(x, v128, 0);
		glCullFace(GL_BACK);
		
		glPushMatrix();
		glScalef(v128, v128, 1);
		glRotatef(rot, 0, 0, 1);
		glRotatef(180, 1, 0, 0);
		glTranslatef(-Shadows.cameraLocation.x / 128, -Shadows.cameraLocation.z / 128, 0);
		
		textureBox.renderStart(false);
		glColor4f(0f, 0.7f, 0f, 1f);
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		
		glPushMatrix();
		glScalef(1f / 128f, 1f / 128f, 1);
		glScalef(8, 8, 1);
		textureBox.renderStart(false);
		glColor4f(1f, 0f, 0f, 1f);
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		glPopMatrix();
		glPopMatrix();
		
		glCullFace(GL_FRONT);
		glScalef(v128 / 16, v128 / 16, 1);
		textureBox.renderStart(false);
		textureBox.renderFinish(GL_TRIANGLES);
		glPopMatrix();
	}
}