package com.shadows.openGL;

import com.shadows.*;
import com.shadows.openGL.graphics.*;
import org.lwjgl.opengl.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.util.glu.GLU.*;

public class OpenGL {
	/**
	 * Sends the fov, aspect ratio,
	 * zNear, and zFar variables
	 * to openGL, and creates the
	 * camera matrix, along with
	 * enabling basic functions
	 * of the engine.
	 */
	public static void setUp() {
		LightHandler.setUp();
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_ALPHA_TEST);
		glAlphaFunc(GL_GREATER, 0.2f);
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		glEnable(GL_TEXTURE_2D);
		glCullFace(GL_BACK);
		glShadeModel(GL_SMOOTH);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void setUpShadowMaps() {
		ShadowMapper.checkCompatibility();
		ShadowMapper.setUp();
		ShadowMapper.setUpFrameBuffer(512);
	}
	
	public static void setUpFog(float fogStart, float fogDepth, float fogDensity) {
		glEnable(GL_FOG);
		glFog(GL_FOG_COLOR, Buffer.asFloatBuffer(new float[] {
				0.5294117647f, 0.80784313725f, 0.98039215686f, 1
		}));
		glFogi(GL_FOG_MODE, GL_LINEAR);
		glHint(GL_FOG_MODE, GL_NICEST);
		glFogf(GL_FOG_START, fogStart);
		glFogf(GL_FOG_END, fogStart + fogDepth);
		glFogf(GL_FOG_DENSITY, fogDensity);
	}

	public static void readyPerspective() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(Shadows.fov, (float) (Display.getWidth()) / (float) (Display.getHeight()), Shadows.zNear, Shadows.zFar);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}

	public static void readyOrtho() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, Display.getWidth(), 0, Display.getHeight(), -1, 1);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
	}
}