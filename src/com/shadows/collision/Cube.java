package com.shadows.collision;

public class Cube {
	private float x, y, z, length, width, height;

	public Cube(float x, float y, float z, float length, float width, float height) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.width = width;
		this.height = height;
	}
	
	public Cube() {
		this(0, 0, 0, 0, 0, 0);
	}
	
	public Cube(Cube c) {
		this(c.getX(), c.getY(), c.getZ(), c.getLength(), c.getWidth(), c.getHeight());
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}

	public float getLength() {
		return length;
	}

	public void setLength(float length) {
		this.length = length;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}
	
	/**
	 * Tests if two cubes intersect in the x and z axis
	 * @param cube
	 * @return intersected
	 */
	public boolean intersects(Cube cube) {
		float x = Math.abs(this.x - cube.getX());
		float z = Math.abs(this.z - cube.getZ());
		
		/*
		float x2 = Math.abs(this.x - (cube.getX() + 1));
		float z2 = Math.abs(this.x - (cube.getZ() + 1));
		*/
		
		boolean a = x < 1f && x >= 0f && z < 1f && z >= 0f;
		/*
		boolean b = x2 < 1 && x2 >= 0 && z < 1 && z >= 0;
		boolean c = x < 1 && x >= 0 && z2 < 1 && z2 >= 0;
		boolean d = x2 < 1 && x2 >= 0 && z2 < 1 && z2 >= 0;
		*/
		
		return a;// || b || c || d;
	}

	public void setBounds(float x, float y, float z, float length, float width, float height) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.length = length;
		this.width = width;
		this.height = height;
	}
	
	public String toString() {
		return "Cube: " + x + ", " + y + ", " + z + ", " + length + ", " + width + ", " + height;
	}
}
