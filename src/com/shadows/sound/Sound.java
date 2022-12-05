package com.shadows.sound;

import java.io.*;
import java.net.*;
import org.lwjgl.util.*;
import com.shadows.*;

import static org.lwjgl.openal.AL10.*;

public class Sound {
	private WaveData data = null;
	private int buffer = 0, source = 0;
	
	/**
	 * Creates a new sound object to play.
	 * @param Sound Source
	 * @throws FileNotFoundException
	 */
	public Sound(String fileSource) throws IOException, URISyntaxException {
		this(fileSource, false);
	}
	
	public Sound(String fileSource, boolean music) throws IOException, URISyntaxException {
		InputStream stream = new FileInputStream(Shadows.directory + fileSource);
		data = WaveData.create(new BufferedInputStream(stream));
		buffer = alGenBuffers();
		if (!music) {
			alBufferData(buffer, data.format, data.data, data.samplerate);
		} else {
			alBufferData(buffer, data.format, data.data, data.samplerate / 8);
		}
		data.dispose();
		source = alGenSources();
		alSourcei(source, AL_BUFFER, buffer);
	}
	
	public void play() {
		alSourcePlay(source);
	}
}