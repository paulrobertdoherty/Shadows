package com.shadows.openGL.graphics;

import java.nio.*;

import org.lwjgl.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import com.shadows.*;
import com.shadows.openGL.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.util.glu.GLU.*;

public class ShadowMapper {
	private static boolean compatible = true;
	public static boolean ambientSupported = false;
	private static int width = 0, height = 0, framebuffer = 0, renderbuffer = 0;
	private static FloatBuffer lightModelView = BufferUtils.createFloatBuffer(16), lightProjection = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Checks the machine if it compatible with shadow maps.
	 */
	public static void checkCompatibility() {
		if (!GLContext.getCapabilities().OpenGL14 || !GLContext.getCapabilities().GL_ARB_framebuffer_object || !GLContext.getCapabilities().GL_ARB_shadow) {
			System.out.println("Your machine is not compatible with shadow maps.  It needs openGL 1.4 the GL_ARB_framebuffer_object extension, and the GL_ARB_shadow extension.");
			compatible = false;
		}
	}
	
	/**
	 * Sets up openGL for shadow mapping
	 */
	public static void setUp() {
		if (compatible) {
			glClearColor(0.5294117647f, 0.80784313725f, 0.98039215686f, 1);
			glPolygonOffset(2, 0);
			/*
			glLightModel(GL_LIGHT_MODEL_AMBIENT, Buffer.asFloatBuffer(new float[] {
					0f, 0f, 0f, 1
			}));
			glLight(GL_LIGHT0, GL_AMBIENT, Buffer.asFloatBuffer(new float[] {
					0.5f, 0.5f, 0.5f, 1
			}));
			glLight(GL_LIGHT0, GL_DIFFUSE, Buffer.asFloatBuffer(new float[] {
					1f, 1f, 1f, 1
			}));
			*/
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
			
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
			
			glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);
			
			/*
			glTexGeni(GL_S, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
			glTexGeni(GL_T, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
			glTexGeni(GL_R, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
			glTexGeni(GL_Q, GL_TEXTURE_GEN_MODE, GL_EYE_LINEAR);
			*/
		}
	}
	
	/**
	 * Sets up the frame buffer object for creating the shadow map depth buffer.
	 */
	public static void setUpFrameBuffer(int maxTextureSize) {
		if (compatible) {
			final int MAX_RENDERBUFFER_SIZE = glGetInteger(GL_MAX_RENDERBUFFER_SIZE);
			final int MAX_TEXTURE_SIZE = glGetInteger(GL_MAX_TEXTURE_SIZE);
			
			if (MAX_TEXTURE_SIZE > maxTextureSize) {
				if (MAX_RENDERBUFFER_SIZE < MAX_TEXTURE_SIZE) {
					width = height = MAX_RENDERBUFFER_SIZE;
				} else {
					width = height = maxTextureSize;
				}
			} else {
				width = height = MAX_TEXTURE_SIZE;
			}
			
			framebuffer = glGenFramebuffers();
			glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
			renderbuffer = glGenRenderbuffers();
			glBindRenderbuffer(GL_RENDERBUFFER, renderbuffer);
			glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
			glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbuffer);
			glDrawBuffer(GL_NONE);
			glReadBuffer(GL_NONE);
			
			int fboStatus = glCheckFramebufferStatus(GL_FRAMEBUFFER);
			if (fboStatus != GL_FRAMEBUFFER_COMPLETE) {
				System.err.println("Framebuffer Error: " + gluErrorString(fboStatus));
			}
			
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
	}
	
	/*
	private static void a() {
		textureBuffer.put(0, dMVP.m00);
		textureBuffer.put(1, dMVP.m01);
		textureBuffer.put(2, dMVP.m02);
		textureBuffer.put(3, dMVP.m03);
	}
	private static void b() {
		textureBuffer.put(0, dMVP.m10);
		textureBuffer.put(1, dMVP.m11);
		textureBuffer.put(2, dMVP.m12);
		textureBuffer.put(3, dMVP.m13);
	}
	private static void c() {
		textureBuffer.put(0, dMVP.m20);
		textureBuffer.put(1, dMVP.m21);
		textureBuffer.put(2, dMVP.m22);
		textureBuffer.put(3, dMVP.m23);
	}
	private static void d() {
		textureBuffer.put(0, dMVP.m30);
		textureBuffer.put(1, dMVP.m31);
		textureBuffer.put(2, dMVP.m32);
		textureBuffer.put(3, dMVP.m33);
	}
	*/
	
	private static FloatBuffer getMatrix(Matrix4f mat) {
		FloatBuffer b = BufferUtils.createFloatBuffer(16);
		
		b.put(1);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(1);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(1);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(0);
		b.put(1);
		
		b.flip();
		
		return b;
	}
	
	/**
	 * Generates texture coordinates
	 * @param b 
	 */
	public static void generateTextureCoords(boolean b) {
		if (compatible) {
			if (!b) {
				glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
				glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_R_TO_TEXTURE);
			} else {
				glMatrixMode(GL_TEXTURE);
				glLoadIdentity();
				glTranslatef(0.5f, 0.5f, 0.5f);
				glScalef(0.5f, 0.5f, 0.5f);
				glMultMatrix(lightProjection);
				glMultMatrix(lightModelView);
				
				Matrix4f temp = new Matrix4f();
				temp.load(lightModelView);
				lightModelView.flip();
				temp.invert();
				glMultMatrix(getMatrix(temp));
				
				glMatrixMode(GL_MODELVIEW);
			}
		}
	}
	
	public static void drawShadowMap(float x, float y, float z) {
		float sceneBoundingRadius =  20;
		float newX = Camera.lightPosition.x - x;
		float newY = Camera.lightPosition.y - y;
		float newZ = Camera.lightPosition.z - z;
		
		float lightToSceneDistance = (float) Math.sqrt(
				newX * newX +
				newY * newY + 
				newZ * newZ
		);
		
		
		float nearPlane = lightToSceneDistance - sceneBoundingRadius;
		if (nearPlane < 0) {
			System.err.println("The near plane is too small to make a shadow map.  It is " + nearPlane + ".");
			return;
		}
		
		float fieldOfView = (float) Math.toDegrees(2 * Math.atan(sceneBoundingRadius / lightToSceneDistance));
		glMatrixMode(GL_PROJECTION);
		
		glPushMatrix();
		glLoadIdentity();
		
		gluPerspective(fieldOfView, 1, nearPlane, nearPlane + sceneBoundingRadius * 2);
		
		glGetFloat(GL_PROJECTION_MATRIX, lightProjection);
		glMatrixMode(GL_MODELVIEW);
		
		glPushMatrix();
		glLoadIdentity();
		
		gluLookAt(Camera.lightPosition.x, Camera.lightPosition.y, Camera.lightPosition.z, x, y, z, 0, 1, 0);
		
		glGetFloat(GL_MODELVIEW_MATRIX, lightModelView);
		
		glViewport(0, 0, width, height);
		
		glActiveTexture(GL_TEXTURE1);
		glBindFramebuffer(GL_FRAMEBUFFER, framebuffer);
		generateTextureCoords(true);
		
		glClear(GL_DEPTH_BUFFER_BIT);
		
		glPushAttrib(GL_ALL_ATTRIB_BITS); {
			glShadeModel(GL_FLAT);
			
			glDisable(GL_LIGHTING);
			glDisable(GL_COLOR_MATERIAL);
			glDisable(GL_NORMALIZE);
			glEnable(GL_CULL_FACE);
			glCullFace(GL_FRONT);
			glUseProgram(0);
			
			glColorMask(false, false, false, false);
			
			Shadows.drawObjects(true);
			
			glCopyTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, 0, 0, width, height, 0);
			glPopMatrix();
			glMatrixMode(GL_PROJECTION);
			glPopMatrix();
			glMatrixMode(GL_MODELVIEW);
			glBindFramebuffer(GL_FRAMEBUFFER, 0);
		}
		glPopAttrib();
		glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
}
