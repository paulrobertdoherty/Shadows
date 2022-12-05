package com.shadows.openGL.drawer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.util.vector.*;
import com.shadows.Buffer;

public class NormalDrawer {
	public int vertexHandle, normalHandle;
	FloatBuffer verticies = null;
	FloatBuffer normals = null;
	private int faces;
	public static final int POINTS = 1;
	public static final int LINES = 2;
	public static final int TRIANGLES = 3;
	
	public NormalDrawer() {
		vertexHandle = glGenBuffers();
		normalHandle = glGenBuffers();
	}
	
	public void begin(int faces) {
		this.faces = faces;
		verticies = BufferUtils.createFloatBuffer(faces * 9);
		normals = BufferUtils.createFloatBuffer(faces * 9);
	}
	
	public void addVertex(float x, float y, float z) throws NullPointerException{
		verticies.put(Buffer.asFloats(new Vector3f(x, y, z)));
	}
	
	public void addVertex(Vector3f v) {
		verticies.put(Buffer.asFloats(v));
	}
	
	public void addNormal(Vector3f n) {
		normals.put(Buffer.asFloats(n));
	}
	
	public void addNormal(float x, float y, float z) throws NullPointerException{
		normals.put(Buffer.asFloats(new Vector3f(x, y, z)));
	}
	
	public void end(int openGLDrawType) {
		verticies.flip();
		normals.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glBufferData(GL_ARRAY_BUFFER, verticies, openGLDrawType);
		glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
		glBufferData(GL_ARRAY_BUFFER, normals, openGLDrawType);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void renderStart() {
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0l);
		glBindBuffer(GL_ARRAY_BUFFER, normalHandle);
		glNormalPointer(GL_FLOAT, 0, 0l);
		glEnableClientState(GL_VERTEX_ARRAY);
		glEnableClientState(GL_NORMAL_ARRAY);
	}
	
	public void renderFinish (int openGLType) {
		glDrawArrays(openGLType, 0, faces * 3);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_NORMAL_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void stop() {
		glDeleteBuffers(vertexHandle);
		glDeleteBuffers(normalHandle);
	}
}