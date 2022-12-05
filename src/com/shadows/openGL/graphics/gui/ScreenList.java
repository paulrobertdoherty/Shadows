package com.shadows.openGL.graphics.gui;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import com.shadows.openGL.graphics.*;
import static org.lwjgl.opengl.GL11.*;

public class ScreenList extends GUIObject {
	private int x, y, width, height;
	private static int lastY, scrollBarOffset;
	private static boolean scrollBarSelected;
	private java.util.List<String> stuff;
	private static Texture texture = null;
	
	public ScreenList(int x, int y, int width, int height, java.util.List<String> stuff) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
		this.stuff = stuff;
		if (texture == null) {
			texture = new Texture("/res/textures/selection.png");
		}
	}
	
	public String renderAndTest() {
		//draw occuluder
		glPushMatrix();
		glScalef(Display.getWidth(), this.x, 1);
		textureBox.renderStart(false);
		glColor4f(0.5294117647f, 0.80784313725f, 0.98039215686f, 1);
		textureBox.renderFinish(GL_TRIANGLES);
		glColor4f(1, 1, 1, 1);
		glPopMatrix();
		
		String returner = null;
		
		//Draw list items
		int heightOfSelections = height / 5;
		
		//scroll bar
		int scrollBarWidth = Display.getWidth() / 32;
		
		if (stuff.size() > 5) {
			float scrollBarHeight = ((float)height / ((float)stuff.size() / 5.0f));
			if (Mouse.getX() > x + width && Mouse.getX() < x + width + scrollBarWidth && Mouse.getY() > (y + height) - (scrollBarHeight + scrollBarOffset) && Mouse.getY() < (y + height) - scrollBarOffset && Mouse.isButtonDown(0)) {
				if (!scrollBarSelected) {
					lastY = Mouse.getY();
					scrollBarSelected = true;
				} else if (Mouse.getY() != lastY) {
					scrollBarOffset = lastY - Mouse.getY();
					if (Math.abs(scrollBarOffset) > height - scrollBarHeight || scrollBarOffset < 0) {
						scrollBarOffset = isPositive(scrollBarOffset) ? (int)(height - scrollBarHeight) : 0;
					}
				}
			}
			
			
			//draw scroll bar
			glPushMatrix();
			glTranslatef(x + width, ((y + height) - scrollBarHeight) - scrollBarOffset, 0);
			glScalef(scrollBarWidth, scrollBarHeight, 1);
			textureBox.renderStart(false);
			glColor4f(0.5f, 0.5f, 0.5f, 1);
			textureBox.renderFinish(GL_TRIANGLES);
			glPopMatrix();
			glColor4f(1, 1, 1, 1);
		}
		
		for (int i = y + (height - heightOfSelections); i >= y + heightOfSelections * (5 - stuff.size()); i -= heightOfSelections) {
			float realI = i + (((float)scrollBarOffset / (float)height) * (heightOfSelections * stuff.size()));
			boolean selected = false;
			
			int index = 5 - (((int)i - y) / heightOfSelections) - 1;
			
			boolean clicked = Mouse.getX() > this.x && Mouse.getY() > y && Mouse.getY() > realI && Mouse.getX() < (this.x + this.width) && Mouse.getY() < realI + heightOfSelections && Mouse.getY() < (y + height) && Mouse.isButtonDown(0);
			boolean alreadySelected = MainMenu.save != null && MainMenu.save.contentEquals(stuff.get(index));
			
			if (clicked || alreadySelected && returner == null) {
				selected = true;
			}
			Color color = null;
			if (selected) {
				color = new Color(0, 1, 1, 1);
			} else {
				color = new Color(1, 1, 1, 1);
			}
			String toDisplay = stuff.get(index);
			drawTextWithBox((int)realI, heightOfSelections, toDisplay, color, selected);
			if (selected) {
				returner = toDisplay;
			}
		}
		
		return returner;
	}
	
	private void drawTextWithBox(int i, int heightOfSelections, String toDisplay, Color color, boolean selected) {
		Text.drawText(this.x + 10, i, 16, 16, toDisplay, color);
		glPushMatrix();
		glTranslatef(x, i, 0);
		glScalef(width, heightOfSelections, 1);
		texture.bind();
		textureBox.renderStart(true);
		textureBox.renderFinish(GL_TRIANGLES);
		glPopMatrix();
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	private boolean isPositive(int num) {
		return Math.abs(num) == num;
	}
	
	/*
	//Draw first occluder
	glPushMatrix();
	glScalef(Display.getWidth(), this.x, 1);
	textureBox.renderStart(false);
	glColor4f(0.5294117647f, 0.80784313725f, 0.98039215686f, 1);
	textureBox.renderFinish(GL_TRIANGLES);
	glColor4f(1, 1, 1, 1);
	glPopMatrix();
	
	//Draw second occluder
	glPushMatrix();
	glTranslatef(0, this.y + this.height, 0);
	glScalef(Display.getWidth(), Display.getHeight() - (this.y + this.height), 1);
	textureBox.renderStart(false);
	glColor4f(0.5294117647f, 0.80784313725f, 0.98039215686f, 1);
	textureBox.renderFinish(GL_TRIANGLES);
	glColor4f(1, 1, 1, 1);
	glPopMatrix();
	*/
}