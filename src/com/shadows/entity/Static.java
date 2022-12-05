package com.shadows.entity;

import com.shadows.collision.*;
import com.shadows.openGL.drawer.*;

public interface Static {
	public void draw();
	public void render();
	public void setLocation(float x, float y, float z);
	public void setX(float x);
	public void setY(float y);
	public void setZ(float z);
	public void setLength(float length);
	public void setWidth(float width);
	public void setHeight(float height);
	public float getX();
	public float getY();
	public float getZ();
	public float getLength();
	public float getWidth();
	public float getHeight();
	public boolean intersects(Static s);
	public void onIntersect(Static s);
	public Cube getCollision();
	public void setCollision(Cube collision);
	public WalkableTerrain getTerrainCollision();
	public boolean hasTerrainCollision();
	public RenderObject getRenderObject();
	public void update(int delta);
}
