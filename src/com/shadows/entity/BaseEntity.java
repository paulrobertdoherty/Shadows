package com.shadows.entity;

import com.shadows.openGL.drawer.*;

public abstract class BaseEntity extends BaseStatic implements Entity{
	protected float dx, dy, dz;
	
	public BaseEntity(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
		this.dx = 0;
		this.dy = 0;
		this.dz = 0;
	}

	public void update(int delta){
		this.x += delta * dx;
		this.y += delta * dy;
		this.z += delta * dz;
	}
	
	public float getDX() {
		return dx;
	}

	public float getDY() {
		return dy;
	}

	public float getDZ() {
		return dz;
	}

	public void setDX(float dx) {
		this.dx = dx;
	}

	public void setDY(float dy) {
		this.dy = dy;
	}

	public void setDZ(float dz) {
		this.dz = dz;
	}
}