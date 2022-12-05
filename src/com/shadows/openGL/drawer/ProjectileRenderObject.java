package com.shadows.openGL.drawer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;

public class ProjectileRenderObject extends RenderObject {
	NormalDrawer drawer = null;
	
	public ProjectileRenderObject() {
		if (drawer == null) {
			drawer = new NormalDrawer();
		}
	}
	
	@Override
	public void draw() {
		drawer.begin(2);
		drawer.addVertex(0, 0, 0);
		drawer.addNormal(0, 0, 0);
		drawer.addVertex(1, 0, 0);
		drawer.addNormal(1, 0, 0);
		drawer.addVertex(1, 1, 0);
		drawer.addNormal(1, 1, 0);
		
		drawer.addVertex(1, 1, 0);
		drawer.addNormal(1, 1, 0);
		drawer.addVertex(0, 1, 0);
		drawer.addNormal(0, 1, 0);
		drawer.addVertex(0, 0, 0);
		drawer.addNormal(0, 0, 0);
		drawer.end(GL_STATIC_DRAW);
	}

	@Override
	public void render(float r, float g, float b, float a) {
		drawer.renderStart();
		glColor4f(r, g, b, a);
		drawer.renderFinish(GL_TRIANGLES);
	}

	@Override
	public void close() {
		drawer.stop();
	}
}