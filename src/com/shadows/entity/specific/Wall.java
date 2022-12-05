package com.shadows.entity.specific;

import com.shadows.entity.*;
import com.shadows.openGL.drawer.*;

import static org.lwjgl.opengl.GL11.*;

public class Wall extends BaseStatic {

	public Wall(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	@Override
	public void render() {
		glEnable(GL_CULL_FACE);
		renderObject.render(0.7f, 0.7f, 0.7f, 1);
		glDisable(GL_CULL_FACE);
	}

	@Override
	public void onIntersect(Static s) {
	}

	@Override
	public void update(int delta) {
		
	}
}