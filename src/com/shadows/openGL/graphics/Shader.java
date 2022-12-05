package com.shadows.openGL.graphics;

import java.io.*;
import org.lwjgl.*;
import org.lwjgl.opengl.*;
import com.shadows.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class Shader {
	public static int loadShaderPair(String vertexLocation, String fragmentLocation) throws LWJGLException, IOException {
		if (GLContext.getCapabilities().OpenGL20) {
			int shaderProgram = glCreateProgram();
			int vertexShader = glCreateShader(GL_VERTEX_SHADER);
			int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
			StringBuilder vertexShaderSource = new StringBuilder();
			StringBuilder fragmentShaderSource = new StringBuilder();
			
			{
			BufferedReader reader = new BufferedReader(new FileReader(Shadows.directory + vertexLocation));
			String line = null;
			while ((line = reader.readLine()) != null) {
				vertexShaderSource.append(line).append('\n');
			}
			reader.close();
			}
			
			BufferedReader reader = new BufferedReader(new FileReader(Shadows.directory + fragmentLocation));
			String line = null;
			while ((line = reader.readLine()) != null) {
				fragmentShaderSource.append(line).append('\n');
			}
			reader.close();
			
			glShaderSource(vertexShader, vertexShaderSource);
			glCompileShader(vertexShader);
			System.out.println("Vertex shader log: " + getShaderLog(vertexShader));
			if (glGetShader(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
				throw new LWJGLException("The vertex shader was not compiled correctly.");
			}
			
			glShaderSource(fragmentShader, fragmentShaderSource);
			glCompileShader(fragmentShader);
			System.out.println("Fragment shader log: " + getShaderLog(fragmentShader));
			
			if (glGetShader(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
				throw new LWJGLException("The fragment shader was not compiled correctly.");
			}
			
			glAttachShader(shaderProgram, vertexShader);
			glAttachShader(shaderProgram, fragmentShader);
			glLinkProgram(shaderProgram);
			glValidateProgram(shaderProgram);
			return shaderProgram;
		} else {
			throw new LWJGLException("Your openGL is too outdated to load shaders.  Your openGL is " + glGetString(GL_VERSION) + ".");
		}
	}
	
	private static String getShaderLog(int shader) {
		return glGetShaderInfoLog(shader, glGetShader(shader, GL_INFO_LOG_LENGTH));
	}
}