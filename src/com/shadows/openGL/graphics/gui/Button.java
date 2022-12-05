package com.shadows.openGL.graphics.gui;

import java.io.*;
import java.net.URISyntaxException;

import org.lwjgl.input.*;

import com.shadows.openGL.graphics.*;
import com.shadows.sound.*;

import static org.lwjgl.opengl.GL11.*;

public class Button extends GUIObject{
	private int x, y, width, height;
	private ButtonEvent b;
	private String label;
	private boolean selected = false, available = false;
	private static boolean clicked = false;
	public static Sound sound = null;

	public Button(ButtonEvent b, String label) {
		if (sound == null) {
			try {
				sound = new Sound("/res/sounds/button.wav");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		this.b = b;
		this.label = label;
	}
	
	public void render(Texture texture, boolean pause) {
		if (!pause) {
			Text.drawText(x + 10, y + 10, 16, 16, label);
		}
		texture.bind();
		glPushMatrix();
		glTranslatef(x, y, 0);
		glScalef(width, height, 1);
		textureBox.renderStart();
		if (selected) {
			glColor4f(0f, 1f, 1, 1);
		} else if (!available) {
			glColor4f(0.5f, 0.5f, 0.5f, 1);
		}
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		glPopMatrix();
		if (pause) {
			Text.drawText(x + 10, y + 10, 16, 16, label);
		}
		available = false;
	}
	
	public void testClick() {
		available = true;
		if (Mouse.getX() >= x && Mouse.getY() >= y && Mouse.getX() <= x + width && Mouse.getY() <= y + height) {
			if (Mouse.isButtonDown(0)) {
				if (!clicked) {
					b.onClick();
					sound.play();
					clicked = true;
				}
			} else {
				clicked = false;
			}
			selected = true;
		} else {
			selected = false;
		}
	}

	public void setBounds(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
}