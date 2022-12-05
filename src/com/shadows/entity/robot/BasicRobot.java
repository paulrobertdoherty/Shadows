package com.shadows.entity.robot;

import java.io.*;
import java.net.*;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.Vector2f;

import com.shadows.*;
import com.shadows.entity.specific.Box;
import com.shadows.entity.specific.Button;
import com.shadows.level.Level;
import com.shadows.model.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.Color;

import static org.lwjgl.opengl.GL11.*;

public class BasicRobot extends Robot {
	private static final float velocity = 0.001f;
	private static Model model = null;
	private static boolean instantiatedBefore = false;
	private Color color = null;
	private Button[] lastButton = null;
	public Vector2f lastCheckpoint = null;

	public BasicRobot(float x, float y, float z, float length, float width, float height, RenderObject renderObject, Color color) {
		super(x, y, z, length, width, height, renderObject);
		Robot.robots.add(this);
		this.color = color;
		instantiate();
	}
	
	public void setCheckpoint(int x, int z) {
		this.checkPointX = x;
		this.checkPointZ = z;
	}
	
	public int getCheckPointX() {
		return checkPointX;
	}
	
	public int getCheckPointZ() {
		return checkPointZ;
	}
	
	public void instantiate() {
		if (!instantiatedBefore) {
			try {
				model = ModelLoader.loadOBJModel("/res/models/BasicRobot.obj");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			this.renderObject = model;
			
			//Controlling the robot
			KeyboardHandler.addBind("robotForward", Keyboard.KEY_I);
			KeyboardHandler.addBind("robotBackward", Keyboard.KEY_K);
			KeyboardHandler.addBind("robotLeft", Keyboard.KEY_J);
			KeyboardHandler.addBind("robotRight", Keyboard.KEY_L);
			KeyboardHandler.addBind("Go Back To Last Checkpoint", Keyboard.KEY_E);
			
			instantiatedBefore = true;
		} else {
			this.renderObject = model;
		}
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	@Override
	public void render() {
		glPushMatrix();
		glTranslatef(x, y, z);
		glScalef(0.5f, 0.5f, 0.5f);
		renderObject.render(color.getRed(), color.getGreen(), color.getBlue(), 1);
		glPopMatrix();
	}
	
	public Color getColor() {
		return color;
	}

	public void update(int delta) {
		super.update(delta);
		
		boolean forward = KeyboardHandler.getKey("robotForward");
		boolean backward = KeyboardHandler.getKey("robotBackward");
		boolean left = KeyboardHandler.getKey("robotLeft");
		boolean right = KeyboardHandler.getKey("robotRight");
		dx = 0;
		dz = 0;
		
		if (!Shadows.pauseMenu) {
			if (forward && !backward && !left && !right) {
				move(velocity, 0);
			}
			if (backward && !forward && !left && !right) {
				move(-velocity, 0);
			}
			if (left && !right && !backward && !forward) {
				move(0, -velocity);
			}
			if (right && !left && !backward && !forward) {
				move(0, velocity);
			}
			if (left && !right && !backward && forward) {
				move(velocity, -velocity);
			}
			if (left && !right && backward && !forward) {
				move(-velocity, -velocity);
			}
			if (!left && right && !backward && forward) {
				move(velocity, velocity);
			}
			if (!left && right && backward && !forward) {
				move(-velocity, velocity);
			}
		}
		
		Button[] collision = collidesWithButton((int)x, (int)z);
		if (collision != null) {
			for (Button b : collision) {
				b.onIntersect(this);
			}
			lastButton = collision;
		} else if (lastButton != null) {
			int wallsNotAbleToBeCoveredUp = 0;
			for (Button b : lastButton) {
				if (!robotInInvisibleWall(b.getTargetX(), b.getTargetZ())) {
					b.onUnIntersect();
					System.out.println("This works, right?");
				} else {
					wallsNotAbleToBeCoveredUp++;
				}
			}
			if (wallsNotAbleToBeCoveredUp == 0) {
				lastButton = null;
			}
		}
	}
	
	private boolean robotInInvisibleWall(int wallX, int wallZ) {
		boolean in = false;
		for (Robot r : robots) {
			int x = (int)r.getX();
			int z = (int)r.getZ();
			if ((x == wallX && z == wallZ) || ((x + 1) == wallX && z == wallZ) || (x == wallX && (z + 1) == wallZ) || ((x + 1) == wallX && (z + 1) == wallZ)) {
				in = true;
			}
		}
		return in;
	}
	
	private Button[] collidesWithButton(int x, int z) {
		if (Level.buttonArray[x][z] != null) {
			return Level.buttonArray[x][z];
		} else if (Level.buttonArray[x + 1][z] != null) {
			return Level.buttonArray[x + 1][z];
		} else if (Level.buttonArray[x][z + 1] != null) {
			return Level.buttonArray[x][z + 1];
		} else if (Level.buttonArray[x + 1][z + 1] != null) {
			return Level.buttonArray[x + 1][z + 1];
		}
		return null;
	}

	private void move(float vx, float vz) {
		//go back to checkpoint
		if (KeyboardHandler.getKey("Go Back To Last Checkpoint") && checkPointX != 0 && checkPointZ != 0) {
			Box b = Level.boxArray[(int)lastCheckpoint.x][(int)lastCheckpoint.y];
			b.setExists(true);
			if (checkPointX != this.x && checkPointZ != this.z) {
				Level.boxes.add(b);
			}
			this.x = checkPointX;
			this.z = checkPointZ;
			inCheckpoint = false;
		}
		
		this.dx = vx;
		this.dz = vz;
	}
}