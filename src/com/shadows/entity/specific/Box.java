package com.shadows.entity.specific;

import java.io.*;
import java.net.*;
import com.shadows.entity.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.*;
import com.shadows.sound.*;

import static org.lwjgl.opengl.GL11.*;

public class Box extends BaseStatic implements Serializable{
	private static final long serialVersionUID = -4164196168401928077L;
	
	private static Sound sound;
	private boolean exists = true;
	private Color color = null;
	
	public Box(float x, float y, float z, float length, float width, float height, RenderObject renderObject, Color color) {
		super(x, y, z, length, width, height, renderObject);
		try {
			if (sound == null) {
				sound = new Sound("/res/sounds/Shoot.wav");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	@Override
	/**
	 * Is not used for this entity, however you SHOULD NOT DELETE IT.
	 */
	public void update(int delta) {
		
	}

	@Override
	public void render() {
		glEnable(GL_CULL_FACE);
		renderObject.render(color.getRed(), color.getGreen(), color.getBlue(), 1);
		glDisable(GL_CULL_FACE);
	}

	@Override
	public void onIntersect(Static s) {
		sound.play();
	}

	public void setExists(boolean exists) {
		this.exists = exists;
	}
	
	public boolean exists() {
		return exists;
	}
}