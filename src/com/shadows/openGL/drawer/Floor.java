package com.shadows.openGL.drawer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Floor extends RenderObject {
	private float width, height, x, y, z;
	private int texTiles = 0;
	private TextureDrawer drawer = null;
	
	public Floor(float width, float height, float x, float y, float z, int texTiles) {
		drawer = new TextureDrawer();
		this.width = width;
		this.height = height;
		this.x = x;
		this.y = y;
		this.z = z;
		this.texTiles = texTiles;
	}
	
	public void draw() {
		drawer.begin(2);
		drawer.addVertex(-width + x, y, -height + z);
		drawer.addUV(0, 0);
		
		drawer.addVertex(-width + x, y, height + z);
		drawer.addUV(0, texTiles);
		
		drawer.addVertex(width + x, y, -height + z);
		drawer.addUV(texTiles, 0);
		
		drawer.addVertex(width + x, y, -height + z);
		drawer.addUV(texTiles, 0);
		
		drawer.addVertex(width + x, y, height + z);
		drawer.addUV(texTiles, texTiles);
		
		drawer.addVertex(-width + x, y, height + z);
		drawer.addUV(0, texTiles);
		drawer.end(GL_STATIC_DRAW);
	}

	@Override
	public void render(float r, float g, float b, float a) {
		drawer.renderStart();
		glColor4f(r, g, b, a);
		drawer.renderFinish(GL_TRIANGLES);
	}
	
	public void close() {
		drawer.stop();
	}
}
