package com.shadows.openGL.graphics;

import java.io.Serializable;

public class Color implements Serializable {
	private static final long serialVersionUID = 9080671693524034449L;
	private float red, green, blue, alpha;

	public Color(float red, float green, float blue, float alpha) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		this.alpha = alpha;
	}

	public Color(java.awt.Color color) {
		this(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}

	public float getAlpha() {
		return alpha;
	}

	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	public boolean matches(float red, float green, float blue, float alpha) {
		return this.red == red && this.green == green && this.blue == blue && this.alpha == alpha;
	}

	public boolean matches(Color color) {
		return matches(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha());
	}
}
