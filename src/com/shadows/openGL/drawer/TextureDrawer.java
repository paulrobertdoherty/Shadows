package com.shadows.openGL.drawer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import java.nio.*;
import org.lwjgl.*;
import org.lwjgl.util.vector.*;
import com.shadows.Buffer;

public class TextureDrawer {
	public int vertexHandle, textureHandle;
	FloatBuffer verticies = null;
	FloatBuffer textures = null;
	private int faces;
	
	public TextureDrawer() {
		vertexHandle = glGenBuffers();
		textureHandle = glGenBuffers();
	}
	
	public void begin(int faces) {
		this.faces = faces;
		verticies = BufferUtils.createFloatBuffer(faces * 9);
		textures = BufferUtils.createFloatBuffer(faces * 6);
	}
	
	public void addVertex(float x, float y, float z) throws NullPointerException{
		verticies.put(Buffer.asFloats(new Vector3f(x, y, z)));
	}
	
	public void addVertex(Vector3f v) {
		verticies.put(Buffer.asFloats(v));
	}
	
	public void addUV(Vector2f n) {
		textures.put(Buffer.asFloats(n));
	}
	
	public void addUV(float u, float v) throws NullPointerException{
		textures.put(Buffer.asFloats(new Vector2f(u, v)));
	}
	
	public void end(int openGLDrawType) {
		verticies.flip();
		textures.flip();
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glBufferData(GL_ARRAY_BUFFER, verticies, openGLDrawType);
		glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
		glBufferData(GL_ARRAY_BUFFER, textures, openGLDrawType);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void renderStart(boolean drawTextures) {
		glBindBuffer(GL_ARRAY_BUFFER, vertexHandle);
		glVertexPointer(3, GL_FLOAT, 0, 0l);
		glEnableClientState(GL_VERTEX_ARRAY);
		if (drawTextures) {
			glBindBuffer(GL_ARRAY_BUFFER, textureHandle);
			glTexCoordPointer(2, GL_FLOAT, 0, 0l);
			glEnableClientState(GL_TEXTURE_COORD_ARRAY);
		}
	}
	
	public void renderStart() {
		renderStart(true);
	}
	
	public void renderFinish (int openGLType) {
		glDrawArrays(openGLType, 0, faces * 3);
		glDisableClientState(GL_VERTEX_ARRAY);
		glDisableClientState(GL_TEXTURE_COORD_ARRAY);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
	}
	
	public void stop() {
		glDeleteBuffers(vertexHandle);
		glDeleteBuffers(textureHandle);
	}
}