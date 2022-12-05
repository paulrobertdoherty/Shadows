package com.shadows.algorithm;

import java.util.*;

public class PerlinNoise {
	private int size = 0, octaves = 0;
	private float persistence = 0;
	private float[][] data = null, values = null;
	private float[][][] octaveList = null;
	private Random random = null;
	
	public PerlinNoise(int size, long seed, int octaves, float persistence) {
		this.octaves = octaves;
		this.size = size;
		this.persistence = persistence;
		random = new Random(seed);
		data = new float[size][size];
		values = new float[size][size];
		octaveList = new float[octaves][size][size];
		
		generateValues();
		for (int i = 0; i < this.octaves; i++) {
			float frequency = (float)Math.pow(2, i + 1);
			float wavelength = this.size / frequency;
			for (int x = 0; x < this.size; x++) {
				for (int y = 0; y < this.size; y++) {
					float actX = x / wavelength;
					float actY = y / wavelength;
					octaveList[i][x][y] = generateNoiseValues(actX, actY, i);
				}
			}
		}
		for (int i = 0; i < octaves; i++) {
			for (int x = 0; x < this.size; x++) {
				for (int y = 0; y < this.size; y++) {
					data[x][y] += octaveList[i][x][y]; 
				}
			}
		}
	}
	
	private void generateValues() {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				values[x][y] = random.nextFloat();
			}
		}
	}

	private float generateNoiseValues(float x, float y, int i) {
		float total = 0;
		
		float amplitude = (float)Math.pow(persistence, i);
		float noise = interpolateNoise(x, y) * amplitude;
		total += noise;
		
		return total;
	}

	private float interpolateNoise(float i, float j) {
		int x = (int)i;
		float floatX = i - x;
		
		int y = (int)j;
		float floatY = j - y;
		
		float v1 = smoothedNoise(x, y);
		float v2 = smoothedNoise(x + 1, y);
		float v3 = smoothedNoise(x, y + 1);
		float v4 = smoothedNoise(x + 1, y + 1);
		
		float i1 = interpolate(v1, v2, floatX);
		float i2 = interpolate(v3, v4, floatX);
		
		return interpolate(i1, i2, floatY);
	}

	private float smoothedNoise(int x, int y) {
		float corners = (controlledNoise(x - 1, y - 1) + controlledNoise(x + 1, y - 1) + controlledNoise(x - 1, y + 1)) + controlledNoise(x + 1, y + 1) / 16;
		float sides = (controlledNoise(x - 1, y) + controlledNoise(x + 1, y) + controlledNoise(x, y-1) + controlledNoise(x, y + 1)) / 8;
		float center = controlledNoise(x, y) / 4;
		return corners + sides + center;
	}
	
	/**
	 * A method for accessing the noise properly.
	 * @param x
	 * @param y
	 * @return Corrected random noise
	 */
	private float controlledNoise(int x, int y) {
		if (x > 0 && y > 0 && x < size && y < size) {
			return (values[x][y] * 2.0f) - 1.0f;
		}
		return 0;   
	}
	
	private float interpolate(float a, float b, float x) {
		double ft = x * Math.PI;
		float f = (float)((1 - Math.cos(ft)) * 0.5);
		
		return a*(1-f)+b*f;
	}

	public float[][] getData() {
		return data;
	}
}