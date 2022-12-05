package com.shadows.openGL;

import java.util.*;
import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;
import com.shadows.*;
import com.shadows.entity.robot.*;
import com.shadows.level.*;
import static org.lwjgl.opengl.GL11.*;

public class Camera {
	public static Vector4f lightPosition = new Vector4f(0, 30, 0, 1);
	private boolean lockedCamera = false;
	private long lastToggle = 0l;
	
	public Camera() {
		KeyboardHandler.addBind("forward", Keyboard.KEY_W);
		KeyboardHandler.addBind("backward", Keyboard.KEY_S);
		KeyboardHandler.addBind("left", Keyboard.KEY_A);
		KeyboardHandler.addBind("right", Keyboard.KEY_D);
		KeyboardHandler.addBind("lock", Keyboard.KEY_G);
	}
	
	public void processKeyboard() {
		boolean forward = KeyboardHandler.getKey("forward");
		boolean backward = KeyboardHandler.getKey("backward");
		boolean left = KeyboardHandler.getKey("left");
		boolean right = KeyboardHandler.getKey("right");
		
		if (forward && !backward && !left && !right) {
			moveFromLook(0, 0, 0.004f * Shadows.g.delta);
		}
		if (backward && !forward && !left && !right) {
			moveFromLook(0, 0, -0.004f * Shadows.g.delta);
		}
		if (left && !right && !backward && !forward) {
			moveFromLook(-0.004f * Shadows.g.delta, 0, 0);
		}
		if (left && !right && !backward && forward) {
			moveFromLook(-0.004f * Shadows.g.delta, 0, 0.004f * Shadows.g.delta);
		}
		if (left && !right && backward && !forward) {
			moveFromLook(-0.004f * Shadows.g.delta, 0, -0.004f * Shadows.g.delta);
		}
		if (!left && right && !backward && forward) {
			moveFromLook(0.004f * Shadows.g.delta, 0, 0.004f * Shadows.g.delta);
		}
		if (!left && right && backward && !forward) {
			moveFromLook(0.004f * Shadows.g.delta, 0, -0.004f * Shadows.g.delta);
		}
		if (right && !left && !backward && !forward) {
			moveFromLook(0.004f * Shadows.g.delta, 0, 0);
		}
		lightPosition.x = Shadows.cameraLocation.x;
		lightPosition.z = Shadows.cameraLocation.z - 15;
		glLight(GL_LIGHT0, GL_POSITION, Buffer.asFloatBuffer(new float[] {
				lightPosition.x, lightPosition.y, lightPosition.z, 1
		}));
		Shadows.player.update(Shadows.g.delta);
	}
	
	public static Vector2f hypotenuseMovement(float x, float vx, float z, float vz) {
		Vector2f vector = new Vector2f(x, z);
		float hypotenuseX = vx;
		float adjacentX = (float) (Math.cos(Math.toRadians(Shadows.cameraRotation.y - 90)) * hypotenuseX);
		float oppositeX = (float) (Math.sin(Math.toRadians(Shadows.cameraRotation.y - 90)) * hypotenuseX);
		vector.y += adjacentX;
		vector.x -= oppositeX;
			
		float hypotenuseZ = vz;
		float adjacentZ = (float) (Math.cos(Math.toRadians(Shadows.cameraRotation.y)) * hypotenuseZ);
		float oppositeZ = (float) (Math.sin(Math.toRadians(Shadows.cameraRotation.y)) * hypotenuseZ);
		vector.y -= adjacentZ;
		vector.x += oppositeZ;
		
		return vector;
	}
	
	private void moveFromLook(float speedX, float speedY, float speedZ) {
		
		float newX = Shadows.cameraLocation.x;
		float newZ = Shadows.cameraLocation.z;
		
		if (lockedCamera) {
			speedX = 0;
			speedZ = 0;
		}
		
		Vector2f vector = hypotenuseMovement(newX, speedX, newZ, speedZ);
		newX = vector.x;
		newZ = vector.y;
		
		if (newX >= 0.3f && newX <= 127.7f) {
			Shadows.cameraLocation.x = newX;
		}
		Shadows.cameraLocation.y = 10;
		if (newZ >= 0.3f && newZ <= 127.7f) {
			Shadows.cameraLocation.z = newZ;
		}
		
	}
	
	public static boolean isColliding(float newX, float newY, float newZ) {
		int x = (int)newX;
		int z = (int)newZ;
		
		Object a = Level.wallArray[x][z];
		Object b = Level.wallArray[x + 1][z];
		Object c = Level.wallArray[x][z + 1];
		Object d = Level.wallArray[x + 1][z + 1];
		
		
		if (collidesWithBorder(newX, newY, newZ, 0.3f)) {
			if (a != null) {
				if (newX - x < 0.9f && newZ - z < 0.9f) {
					return true;
				}
			}
			if (b != null) {
				if (newX - x > 0.1f && newZ - z < 0.9f) {
					return true;
				}
			}
			if (c != null) {
				if (newX - x < 0.9f && newZ - z > 0.1f) {
					return true;
				}
			}
			if (d != null) {
				if (newX - x > 0.1f && newZ - z > 0.1f) {
					return true;
				}
			}
			return false;
		} else if (newZ < 127.7f && newZ > 0.3f && newX > 0.3f && newX < 127.7f) {
			return false;
		}
		return true;
	}
	
	private static boolean collidesWithBorder(float x, float y, float z, float room) {
		return x < 127 - room && z < 127 - room && x > room && z > room;
	}
	
	private int indexOfFocusedRobot = 0;
	private boolean focusedRobot = false;
	
	private void handleLockedCamera() {
		if (Robot.robots.size() > 0) {
			if (Robot.robots.size() > 1 && !focusedRobot) {
				indexOfFocusedRobot = new Random().nextInt(Robot.robots.size());
				focusedRobot = true;
			}
			
			Robot optimalRobot = Robot.robots.get(indexOfFocusedRobot);
			
			if (!Shadows.pauseMenu && lockedCamera) {
				//It once was calculated, but now it's just looking down
				Shadows.cameraRotation = new Vector3f((float)Math.toDegrees(Math.atan(Math.abs(5f / (optimalRobot.getX() - Shadows.cameraLocation.x)))), 90, 0);
			}
			
			Shadows.cameraLocation.x = optimalRobot.getX();
			Shadows.cameraLocation.z = optimalRobot.getZ();
		}
	}

	public void processMouse(float mouseSpeed, float maxLookUp, float maxLookDown) {
		if (Mouse.isButtonDown(0) && !Shadows.pauseMenu) {
			Mouse.setGrabbed(true);
		} else {
			mouseSpeed /= 2;
		}
		
		//toggle locked camera if pressing g
		if (KeyboardHandler.getKey("lock") && System.currentTimeMillis() - lastToggle > 500) {
			if (!lockedCamera) {
				lockedCamera = true;
				focusedRobot = false;
			} else {
				lockedCamera = false;
			}
			lastToggle = System.currentTimeMillis();
		}
		handleLockedCamera();
		if (lockedCamera) {
			return;
		}
		
		float mouseDx = Mouse.getDX() * mouseSpeed * 0.16f;
		float mouseDy = Mouse.getDY() * mouseSpeed * 0.16f;
		
		if (Shadows.cameraRotation.y + mouseDx >= 360) {
			Shadows.cameraRotation.y = (float) (Shadows.cameraRotation.y + mouseDx - 360);
		} else if (Shadows.cameraRotation.y + mouseDx < 0) {
			Shadows.cameraRotation.y = (float) (360 - Shadows.cameraRotation.y + mouseDx);
		} else {
			Shadows.cameraRotation.y += mouseDx;
		}
		if (Shadows.cameraRotation.x - mouseDy >= maxLookDown && Shadows.cameraRotation.x - mouseDy <= maxLookUp) {
			Shadows.cameraRotation.x -= mouseDy;
		} else if (Shadows.cameraRotation.x - mouseDy < maxLookDown) {
			Shadows.cameraRotation.x = (float) maxLookDown;
		} else if (Shadows.cameraRotation.x - mouseDy > maxLookUp) {
			Shadows.cameraRotation.x = (float) maxLookUp;
		}
	}

	public void move() {
		glRotatef(Shadows.cameraRotation.x, 1, 0, 0);
		glRotatef(Shadows.cameraRotation.y, 0, 1, 0);
		glRotatef(Shadows.cameraRotation.z, 0, 0, 1);
		glTranslatef(-Shadows.cameraLocation.x, -Shadows.cameraLocation.y, -Shadows.cameraLocation.z);
	}
}