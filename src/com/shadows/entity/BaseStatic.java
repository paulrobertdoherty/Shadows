package com.shadows.entity;

import com.shadows.collision.*;
import com.shadows.openGL.drawer.*;

public abstract class BaseStatic implements Static{
	protected float x, y, z, length, width, height;
	protected Cube collision = new Cube();
	protected RenderObject renderObject;
	
	public RenderObject getRenderObject() {
		return renderObject;
	}

	public void setRenderObject(RenderObject renderObject) {
		this.renderObject = renderObject;
	}

	protected boolean terrain = false;
	protected WalkableTerrain terrainCollision = null;
	
	public BaseStatic(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.width = width;
		this.height = height;
		this.renderObject = renderObject;
	}

	public void setLocation(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setZ(float z) {
		this.z = z;
	}

	public void setLength(float length) {
		this.length = length;
	}
	
	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public float getZ() {
		return z;
	}
	
	public float getLength() {
		return length;
	}

	public float getHeight() {
		return height;
	}

	public float getWidth() {
		return width;
	}
	
	public void setCollision(Cube collision) {
		this.collision = collision;
	}
	
	public Cube getCollision() {
		return collision;
	}
	
	public boolean hasTerrainCollision() {
		return terrain;
	}
	
	public WalkableTerrain getTerrainCollision() {
		if (terrain) {
			return terrainCollision;
		}
		return null;
	}
	
	public void setTerrainCollision(float[][] points, int tiles, float size) {
		terrain = true;
		terrainCollision = new WalkableTerrain(points, tiles, size);
	}

	public boolean intersects(Static s) {
		collision.setBounds(x, y, z,
				collision.getLength(), collision.getWidth(), collision.getHeight());
		if (!s.hasTerrainCollision()) {
			return collision.intersects(s.getCollision());
		} else {
			return s.getTerrainCollision().canWalk(collision);
		}
	}
}