package com.shadows;

import java.io.*;
import com.shadows.networking.UDPClient;

public class FileWriter {
	private FileOutputStream writer = null;
	
	public FileWriter(final String directory) throws IOException {
		constructor(Shadows.directory, directory);
	}
	
	private void constructor(String directory, String fileName) throws IOException {
		File fileLoc = new File(Shadows.directory.substring(0, Shadows.directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves/" + fileName);
		writer = new FileOutputStream(fileLoc);
	}
	
	public void write(String text) throws IOException {
		String newText = text + "\n";
		writer.write(newText.getBytes());
	}
	
	public void flush() throws IOException {
		writer.flush();
	}

	public void write(byte[] bytes) throws IOException {
		writer.write(bytes);
	}
	
	public void writeObject(Object save) throws IOException {
		writer.write(UDPClient.serialize(save));
	}
}