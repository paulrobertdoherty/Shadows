package com.shadows.openGL.graphics;

import java.io.*;
import java.nio.*;
import org.lwjgl.*;
import com.shadows.*;

import de.matthiasmann.twl.utils.*;
import de.matthiasmann.twl.utils.PNGDecoder.*;
import static org.lwjgl.opengl.GL11.*;

public class Texture {
	private int texture = 0, width = 0, height = 0;
	
	/**
	 * The constructor for the Texture class, which loads the texture into openGL.
	 * @param path
	 */
	public Texture(String path) {
		try {
			InputStream stream = new FileInputStream(Shadows.directory + path);
			PNGDecoder decoder = new PNGDecoder(stream);
			ByteBuffer buffer = BufferUtils.createByteBuffer(4 * decoder.getWidth() * decoder.getHeight());
			width = decoder.getWidth();
			height = decoder.getHeight();
			decoder.decode(buffer, decoder.getWidth() * 4, Format.RGBA);
			buffer.flip();
			stream.close();
			texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);
			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, decoder.getWidth(), decoder.getHeight(), 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glBindTexture(GL_TEXTURE_2D, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public int getTextureNumber() {
		return texture;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, texture);
	}

	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
}