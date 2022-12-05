package com.shadows.entity;


public interface Entity {
	public float getDX();
	public float getDY();
	public float getDZ();
	public void setDX(float dx);
	public void setDY(float dy);
	public void setDZ(float dz);
	public void update(int delta);
}
