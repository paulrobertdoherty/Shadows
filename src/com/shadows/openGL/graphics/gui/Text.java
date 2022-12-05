package com.shadows.openGL.graphics.gui;

import java.text.*;
import java.util.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Text {
	private static Texture bitmapTexture = null;
	private static DecimalFormat format = new DecimalFormat("#.##");
	private static float grid_size = 0;
	private static HashMap<Character, TextureDrawer> characters = new HashMap<Character, TextureDrawer>();
	
	public static void setUp(String bitmapLocation, int gridSize) {
		bitmapTexture = new Texture(bitmapLocation);
		grid_size = gridSize;
	}
	
	public static void drawText(float x, float y, float charWidth, float charHeight, String text, Color color) {
		glBlendFunc(GL_ONE, GL_ONE);
		bitmapTexture.bind();
		if (text.contains(":n:")) {
			String[] lines = text.split(":n:");
			float startPos = y - ((lines.length * charWidth) / 2);
			
			for (int i = 0; i < lines.length; i++) {
				String line = lines[(lines.length - i) - 1];
				String actualLine = line;
				if (line.contains(":c:")) {
					actualLine = line.split(":c:")[1];
				}
				glPushMatrix();
				glTranslatef(x - (actualLine.length() * (charWidth / 2)), startPos + (i * charWidth), 0);
				drawChars(charWidth, charHeight, actualLine, getTextColorChange(line, color));
				glColor4f(1, 1, 1, 1);
				glPopMatrix();
			}
		} else {
			glPushMatrix();
			glTranslatef(x, y, 0);
			drawChars(charWidth, charHeight, text, color);
			glPopMatrix();
		}
		glBindTexture(GL_TEXTURE_2D, 0);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	private static Color getTextColorChange(String string, Color color) {
		if (!string.contains(":c:")) {
			return color;
		}
		Color newColor = new Color(1, 1, 1, 1);
		String line = string.split(":c:")[1];
		if (line.contentEquals("UNSATISFACTORY")) {
			newColor.setGreen(0.5f);
			newColor.setBlue(0.5f);
		} else if (line.contentEquals("FAILED")) {
			newColor.setGreen(0);
			newColor.setBlue(0);
		} else if (line.contentEquals("SATISFACTORY")) {
			newColor.setRed(0.5f);
			newColor.setBlue(0.5f);
		} else if (line.contentEquals("AMAZING")) {
			newColor.setRed(0);
			newColor.setBlue(0);
		}
		return newColor;
	}

	public static void drawText(float x, float y, float charWidth, float charHeight, String text) {
		drawText(x, y, charWidth, charHeight, text, new Color(1, 1, 1, 1));
	}
	
	private static void drawChars(float width, float height, String chars, Color color) {
		for (int i = 0; i < chars.length(); i++) {
			glPushMatrix();
			glTranslatef(i * width, 0, 0);
			glScalef(width, height, 1);
			char currentChar = chars.charAt(i);
			if (!characters.containsKey(currentChar)) {
				TextureDrawer drawer = new TextureDrawer();
				drawer.begin(2);
				
				int asciiCode = (int)currentChar;
				final float cellSize = 1 / grid_size;
				
				float cellX = ((int)(asciiCode % grid_size)) * cellSize;
				float cellY = ((int)(asciiCode / grid_size)) * cellSize;
				
				drawer.addUV(cellX, cellY);
				drawer.addVertex(0, 1, 0);
				
				drawer.addUV(cellX + cellSize, cellY);
				drawer.addVertex(1, 1, 0);
				
				drawer.addUV(cellX + cellSize, cellY + cellSize);
				drawer.addVertex(1, 0, 0);
				
				drawer.addUV(cellX + cellSize, cellY + cellSize);
				drawer.addVertex(1, 0, 0);
				
				drawer.addUV(cellX, cellY + cellSize);
				drawer.addVertex(0, 0, 0);
				
				drawer.addUV(cellX, cellY);
				drawer.addVertex(0, 1, 0);
				
				drawer.end(GL_STATIC_DRAW);
				drawer.renderStart();
				glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
				drawer.renderFinish(GL_TRIANGLES);
				characters.put(currentChar, drawer);
			} else {
				characters.get(currentChar).renderStart();
				glColor4f(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
				characters.get(currentChar).renderFinish(GL_TRIANGLES);
			}
			glPopMatrix();
		}
	}
	
	public static String decimalToString(double input) {
		 return format.format(input);
	}
}