package com.shadows.entity.specific;

import com.shadows.entity.*;
import com.shadows.entity.robot.*;
import com.shadows.level.*;
import com.shadows.openGL.drawer.*;

public class Button extends BaseStatic {
	private int targetX, targetZ;
	private Wall hiddenWall = null;
	private Box hiddenBox = null;
	
	public Button(float x, float y, float z, float length, float width, float height, RenderObject renderObject, int targetX, int targetZ) {
		super(x, y, z, length, width, height, renderObject);
		this.targetX = targetX;
		this.targetZ = targetZ;
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	public int getTargetX() {
		return targetX;
	}

	public int getTargetZ() {
		return targetZ;
	}

	@Override
	public void render() {
		renderObject.render(0.9f, 0.9f, 0.9f, 1);
	}

	@Override
	public void onIntersect(Static s) {
		if (s instanceof BasicRobot) {
			if (Level.wallArray[targetX][targetZ] != null) {
				Level.walls.remove(Level.wallArray[targetX][targetZ]);
				hiddenWall = Level.wallArray[targetX][targetZ];
				Level.wallArray[targetX][targetZ] = null;
			} else {
				Level.boxes.remove(Level.boxArray[targetX][targetZ]);
				hiddenBox = Level.boxArray[targetX][targetZ];
				Level.boxArray[targetX][targetZ] = null;
			}
		}
	}
	
	public void onUnIntersect() {
		if (hiddenWall != null) {
			Level.walls.add(hiddenWall);
			Level.wallArray[targetX][targetZ] = hiddenWall;
			hiddenWall = null;
		}
		if (hiddenBox != null) {
			Level.boxes.add(hiddenBox);
			Level.boxArray[targetX][targetZ] = hiddenBox;
			hiddenBox = null;
		}
	}

	@Override
	public void update(int delta) {}
}