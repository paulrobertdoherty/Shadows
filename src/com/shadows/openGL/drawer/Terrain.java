package com.shadows.openGL.drawer;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL11.*;
import java.awt.Color;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;

import com.shadows.Shadows;

public class Terrain extends RenderObject {
	NormalDrawer drawer = null;
	public int tiles;
	public float size;
	private float[][] heightmap;
	
	/**
	 * Creates terrain from a heightmap image.
	 * @param size
	 * @param path
	 * @param min
	 * @param max
	 * @throws IOException
	 */
	public Terrain(float size, String path, float min, float max) throws IOException{
		drawer = new NormalDrawer();
		this.size = size;
		this.heightmap = loadHeightmap(path, min, max);
	}
	
	/**
	 * A method for getting the heightmap from the Terrain class.
	 * @return The heightmap.
	 */
	public float[][] getHeightmap() {
		return heightmap;
	}
	
	/**
	 * Creates terrain from a float array, usually from perlin noise.
	 * @param size
	 * @param heightmap
	 * @param min
	 * @param max
	 */
	public Terrain(float size, float[][] heightmap, float min, float max) {
		drawer = new NormalDrawer();
		this.size = size;
		this.heightmap = new float[heightmap.length][heightmap.length];
		for (int x = 0; x < heightmap.length; x++) {
			for (int y = 0; y < heightmap.length; y++) {
				float value = heightmap[x][y];
				value = (value + 1.0f) / 2.0f;
				value *= max;
				value += min;
				this.heightmap[x][y] = value;
			}
		}
		tiles = this.heightmap[0].length - 1;
	}
	
	/**
	 * Draws the terrain onto a vertex buffer object.
	 */
	public void draw() {
		drawer.begin(2 * (tiles * tiles));
		float tileSize = size / (tiles + 1);
		for (int i = 0; i < tiles; i++) {
			float x = tileSize + (i * (tileSize * 2));
			float x2 = -tileSize + (i * (tileSize * 2));
			for (int j = 0; j < tiles; j++) {
				float z = tileSize + (j * (tileSize * 2));
				float z2 = -tileSize + (j * (tileSize * 2));
				drawer.addVertex(x2, heightmap[i][j], z2);
				drawer.addNormal(x2, 1.0f, z2);
				
				drawer.addVertex(x, heightmap[i + 1][j + 1], z);
				drawer.addNormal(x, 1.0f, z);
				
				drawer.addVertex(x2, heightmap[i][j + 1], z);
				drawer.addNormal(x2, 1.0f, z);
				
				drawer.addVertex(x, heightmap[i + 1][j + 1], z);
				drawer.addNormal(x, 1.0f, z);
				
				drawer.addVertex(x, heightmap[i + 1][j], z2);
				drawer.addNormal(x, 1.0f, z2);
				
				drawer.addVertex(x2, heightmap[i][j], z2);
				drawer.addNormal(x2, 1.0f, z2);
			}
		}
		drawer.end(GL_STATIC_DRAW);
	}
	
	/**
	 * Gets a height map from a .png file.
	 * @param path
	 * @return Height map array
	 * @throws IOException
	 */
	private float[][] loadHeightmap(String path, float min, float max) throws IOException {
		BufferedImage reader = ImageIO.read(new File(path));
		int width = reader.getWidth();
		int height = reader.getHeight();
		int[] pixels = new int[width * height];
		reader.getRGB(0, 0, width, height, pixels, 0, width);
		float[][] heightmap = getHeightmap(width, height, pixels, min, max);
		return heightmap;
	}
	
	private float[][] getHeightmap(int width, int height, int[] pixels, float min, float max) {
		float[][] heightmap = new float[width][height];
		tiles = width - 1;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				Color pixel = new Color(pixels[x + y * width]);
				int r = pixel.getRed();
				int g = pixel.getGreen();
				int b = pixel.getBlue();
				
				if (r == g && r == b && g == b) {
					float brightness = (float) (b);
					brightness /= 256.0f;
					brightness *= max;
					brightness += min;
					heightmap[x][y] = brightness;
				}
			}
		}
		return heightmap;
	}

	/**
	 * Renders the vbo for the terrain.
	 */
	@Override
	public void render(float r, float g, float b, float a) {
		Shadows.terrainTexture.bind();
		drawer.renderStart();
		glColor4f(r, g, b, a);
		drawer.renderFinish(GL_TRIANGLES);
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public void close() {
		drawer.stop();
	}
}