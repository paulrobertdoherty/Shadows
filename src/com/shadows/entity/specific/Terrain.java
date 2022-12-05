package com.shadows.entity.specific;

import com.shadows.entity.*;
import com.shadows.openGL.drawer.*;
import static org.lwjgl.opengl.GL11.*;

public class Terrain extends BaseStatic {

	public Terrain(float x, float y, float z, float length, float width,
			float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	@Override
	public void render() {
		glDisable(GL_LIGHTING);
		renderObject.render(0.4f, 0.4f, 0.4f, 1f);
		glEnable(GL_LIGHTING);
	}

	@Override
	public void onIntersect(Static s) {
		
	}

	@Override
	public void update(int delta) {
		
	}
}