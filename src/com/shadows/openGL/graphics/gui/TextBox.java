package com.shadows.openGL.graphics.gui;

import org.lwjgl.input.*;
import com.shadows.*;
import com.shadows.openGL.graphics.*;
import static org.lwjgl.opengl.GL11.*;

public class TextBox extends GUIObject{
	private int x, y, width, height;
	private boolean selected = false;
	private String textToReturn = "";

	public TextBox(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public TextBox(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void render(Texture texture) {
		if (texture != null) {
			if (width == 0) {
				width = texture.getWidth();
			}
			if (height == 0) {
				height = texture.getHeight();
			}
		}
		drawText();
		texture.bind();
		Render(new Color(1, 1, 1, 1));
	}
	
	private void Render(Color color) {
		glPushMatrix();
		glTranslatef(x, y, 0);
		glScalef(width, height, 1);
		textureBox.renderStart();
		glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		glPopMatrix();
	}
	
	public void testClick() {
		if (Mouse.isButtonDown(0)) {
			if (Mouse.getX() >= x && Mouse.getY() >= y && Mouse.getX() <= x + width && Mouse.getY() <= y + height) {
				selected = true;
			} else {
				selected = false;
			}
		}
	}
	
	private void drawText() {
		if (selected) {
			KeyboardHandler.isTyping = true;
			String text = KeyboardHandler.getText();
			if (text.length() == 0 && (int)(System.currentTimeMillis() / 500) % 2 == 0) {
				Text.drawText(x, y, 16, 16, "|");
			} else {
				StringBuilder temp = new StringBuilder();
				temp.append(text);
				if (temp.length() > width / 16) {
					temp.setLength(width / 16);
				}
				if ((int)(System.currentTimeMillis() / 500) % 2 == 0) {
					temp.append("|");
				}
				Text.drawText(x, y, 16, 16, temp.toString());
				
				
				if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
					if (temp.toString().endsWith("|")) {
						temp.setLength(temp.length() - 1);
					}
					if (temp.toString().endsWith("\n")) {
						temp.setLength(temp.length() - 1);
					}
					textToReturn = temp.toString();
					KeyboardHandler.setText("");
					KeyboardHandler.isTyping = false;
				}
			}
		} else {
			KeyboardHandler.isTyping = false;
		}
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public String getText() {
		return textToReturn;
	}

	public void render(Color color) {
		drawText();
		Render(color);
	}

	public boolean getSelected() {
		return selected;
	}

	public void setText(String text) {
		textToReturn = text;
	}

	public void setBounds(int x, int y) {
		this.x = x;
		this.y = y;
	}
}