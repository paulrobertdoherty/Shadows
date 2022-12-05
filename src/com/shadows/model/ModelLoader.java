package com.shadows.model;

import java.io.*;
import java.net.*;
import org.lwjgl.util.vector.*;
import com.shadows.*;

public class ModelLoader {
	
	/**
	 * Loads a model from a wavefront .OBJ file.
	 * @param path
	 * @return Model
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static Model loadOBJModel(String path) throws FileNotFoundException, IOException, URISyntaxException {
		InputStream stream = new FileInputStream(Shadows.directory + path);
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		Model model = new Model();
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("v ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.vertices.add(new Vector3f(x, y, z));
			} else if (line.startsWith("vn ")) {
				float x = Float.valueOf(line.split(" ")[1]);
				float y = Float.valueOf(line.split(" ")[2]);
				float z = Float.valueOf(line.split(" ")[3]);
				model.normals.add(new Vector3f(x, y, z));
			} else if (line.startsWith("f ")) {
				Vector3f vertexIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("//")[0]), Float.valueOf(line.split(" ")[2].split("//")[0]), Float.valueOf(line.split(" ")[3].split("//")[0]));
				Vector3f normalIndices = new Vector3f(Float.valueOf(line.split(" ")[1].split("//")[1]), Float.valueOf(line.split(" ")[2].split("//")[1]), Float.valueOf(line.split(" ")[3].split("//")[1]));
				model.faces.add(new Face(vertexIndices, normalIndices));
			}
		}
		reader.close();
		return model;
	}
}