package com.shadows.entity.robot;

import java.util.*;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.*;

import com.shadows.*;
import com.shadows.collision.*;
import com.shadows.entity.*;
import com.shadows.entity.specific.*;
import com.shadows.openGL.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.Color;

public abstract class Robot extends BaseEntity {
	public static List<Robot> robots = new ArrayList<Robot>();
	public static int selectionX, selectionZ;
	private static long lastPlacementOfRobot = 0l;
	public int checkPointX, checkPointZ;
	public boolean inCheckpoint = false;

	protected Robot(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
	}
	
	public void setInCheckpoint(boolean inCheckpoint) {
		this.inCheckpoint = inCheckpoint;
	}
	
	public void update(int delta) {
		super.update(delta);
		x -= dx * delta;
		z -= dz * delta;
		
		//sees if the robot is in a checkpoint, and does not allow the robot to continue if so
		boolean allowed = !isAllowedToContinue();
		
		if (!Camera.isColliding(x + (dx * delta), y, z) && !intersectsOtherRobots(this, dx * delta, 0) && allowed) {
			x += dx * delta;
		}
		if (!Camera.isColliding(x, y, z + (dz * delta)) && !intersectsOtherRobots(this, 0, dz * delta) && allowed) {
			z += dz * delta;
		}
		
		MainPlayer.testCollision(x, y, z, this);
	}
	
	/**
	 * Checks if either all or no passing blocks have been taken, and allows you to move based on that.
	 * @return What do you think?
	 */
	private boolean isAllowedToContinue() {
		return inCheckpoint;
	}

	private boolean intersectsOtherRobots(Robot robot, float xDif, float zDif) {
		//change instance for testing collision if accelerated
		robot.setX(robot.getX() + xDif);
		robot.setZ(robot.getZ() + zDif);
		
		boolean intersects = false;
		
		for (Robot r : robots) {
			if (robot != r && !intersects) {
				intersects = robot.intersects(r);
				if (intersects && Keyboard.isKeyDown(Keyboard.KEY_H)) {
					System.out.println(robot.getX() + ", " + robot.getZ() + ", " + r.getX() + ", " + r.getZ());
				}
			}
		}
		
		//change back instance to use
		robot.setX(robot.getX() - xDif);
		robot.setZ(robot.getZ() - zDif);
		return intersects;
	}

	public static void testForRobotPlacement() {
		Vector2f selection = getSelection();
		selectionX = (int) (selection.y + Shadows.cameraLocation.x);
		selectionZ = (int) (selection.x + Shadows.cameraLocation.z);
		
		if (Mouse.isButtonDown(1) && !Camera.isColliding(selectionX, 0.5f, selectionZ) && System.currentTimeMillis() - lastPlacementOfRobot > 500) {
			createRobot(selectionX, selectionZ, new Color(0, 0, 0, 1));
			lastPlacementOfRobot = System.currentTimeMillis();
		}
	}

	public static void createRobot(int x, int z, Color color) {
		BasicRobot robot = new BasicRobot(x, 0, z, 1, 1, 1, null, color);
		robot.draw();
		robot.setCollision(new Cube(x, 0, z, 1, 1, 1));
	}
	
	public static void renderRobots() {
		for (Robot r : robots) {
			r.render();
		}
	}
	
	public static void updateRobots(int delta) {
		for (Robot r : robots) {
			r.update(delta);
		}
	}

	private static Vector2f getSelection() {
		float xSelection = (float)(Math.tan(Math.toRadians(90.0f - Shadows.cameraRotation.x))) * 10;
		
		float fixedYRot = 90.0f - Shadows.cameraRotation.y;
		if (fixedYRot < 0) {
			fixedYRot *= -1;
		}
		if (fixedYRot >= 360) {
			fixedYRot -= 360;
		}
		
		Vector2f selection = new Vector2f();
		//really x
		selection.y = xSelection * (float)Math.cos(Math.toRadians(fixedYRot));
		
		//really z
		selection.x = (xSelection * (float)Math.sin(Math.toRadians(fixedYRot))) + 1;
		return selection;
	}

	@Override
	public abstract void draw();

	@Override
	public abstract void render();

	@Override
	public void onIntersect(Static s) {}
}