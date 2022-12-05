package com.shadows.openGL.drawer;

public abstract class RenderObject {
	public abstract void draw();
	public abstract void render(float r, float g, float b, float a);
	public abstract void close();
}
