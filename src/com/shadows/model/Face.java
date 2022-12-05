package com.shadows.model;

import org.lwjgl.util.vector.*;

public class Face {
	public Vector3f vertex = new Vector3f(0, 0, 0); //Three indicies (Plural for index), not vertices or normals.
	public Vector3f normal = new Vector3f(0, 0, 0);
	
	public Face(Vector3f vertex, Vector3f normal) {
		this.vertex = vertex;
		this.normal = normal;
	}
}