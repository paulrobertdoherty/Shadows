package com.shadows.model;

import java.util.*;
import org.lwjgl.util.vector.*;
import com.shadows.openGL.drawer.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;

public class Model extends RenderObject {
	public List<Vector3f> vertices = new ArrayList<Vector3f>();
	public List<Vector3f> normals = new ArrayList<Vector3f>();
	public List<Face> faces = new ArrayList<Face>();
	public NormalDrawer drawer = null;
	private int shininess = 0;
	
	/**
	 * Draws the model to the VBO
	 */
	public void draw() {
		if (drawer == null) {
			drawer = new NormalDrawer();
			drawer.begin(this.faces.size());
			for (Face f : this.faces) {
				Vector3f n1 = this.normals.get((int)f.normal.x - 1);
				drawer.addNormal(n1);
				
				Vector3f v1 = this.vertices.get((int)f.vertex.x - 1);
				drawer.addVertex(v1);
				
				
				Vector3f n2 = this.normals.get((int)f.normal.y - 1);
				drawer.addNormal(n2);
				
				Vector3f v2 = this.vertices.get((int)f.vertex.y - 1);
				drawer.addVertex(v2);
				
				
				Vector3f n3 = this.normals.get((int)f.normal.z - 1);
				drawer.addNormal(n3);
				
				Vector3f v3 = this.vertices.get((int)f.vertex.z - 1);
				drawer.addVertex(v3);
			}
			drawer.end(GL_STATIC_DRAW);
		}
	}
	
	public void setShininess(int shininess) {
		this.shininess = shininess;
	}

	@Override
	public void render(float r, float g, float b, float a) {
		drawer.renderStart();
		glColor4f(r, g, b, a);
		glMaterialf(GL_FRONT, GL_SHININESS, shininess);
		drawer.renderFinish(GL_TRIANGLES);
	}
	
	public void close() {
		drawer.stop();
	}
}