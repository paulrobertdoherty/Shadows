package com.shadows;

import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.util.vector.*;

public class Buffer {
	/**
	 * A convenience method for turning a float array into a
	 * float buffer for OpenGL.
	 * @param array
	 * @return FloatBuffer
	 */
	public static FloatBuffer asFloatBuffer(float[] array) {
		FloatBuffer buffer = BufferUtils.createFloatBuffer(array.length);
		buffer.put(array);
		buffer.flip();
		return buffer;
	}
	
	/**
	 * A convenience method for converting a vector
	 * into a float array for OpenGL.
	 * @param vector
	 * @return array
	 */
	public static float[] asFloats(Vector3f vector) {
		return new float[] {
			vector.x, vector.y, vector.z	
		};
	}
	
	/**
	 * A convenience method for converting a vector
	 * into a float array for OpenGL.
	 * @param n
	 * @return array
	 */
	public static float[] asFloats(Vector2f vector) {
		return new float[] {
			vector.x, vector.y	
		};
	}
	
	/**
	 * A convenience method for converting a vector
	 * into a float array for OpenGL.
	 * @param vector
	 * @return array
	 */
	public static float[] asFloats(Vector4f vector) {
		return new float[] {
			vector.x, vector.y, vector.z, 1	
		};
	}
}
