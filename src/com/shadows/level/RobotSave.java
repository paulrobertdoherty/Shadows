package com.shadows.level;

import java.io.*;
import com.shadows.openGL.graphics.*;

public class RobotSave implements Serializable {
	private static final long serialVersionUID = 4479797923495103969L;
	private float x, z;
	private Color color;
	
	public RobotSave(float x, float z, Color color) {
		this.x = x;
		this.z = z;
		this.color = color;
	}
	
	public float getX() {
		return x;
	}
	
	public float getZ() {
		return z;
	}
	
	public Color getColor() {
		return color;
	}
}