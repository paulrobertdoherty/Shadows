package com.shadows.entity.specific;

import com.shadows.entity.*;
import com.shadows.openGL.drawer.*;

public class Player extends BaseEntity {
	private static boolean drawn = false;
	
	public Player(float x, float y, float z, float length, float width, float height, RenderObject model) {
		super(x, y, z, length, width, height, model);
	}
	
	public void update(int delta) {
		super.update(delta);
		MainPlayer.testCollision(this.x, this.y, this.z, this);
		collision.setX(x - 0.5f);
		collision.setY(y);
		collision.setZ(z - 0.5f);
		collision.setLength(length);
		collision.setWidth(width);
		collision.setHeight(height);
	}

	@Override
	public void draw() {
		if (!drawn && renderObject != null) {
			renderObject.draw();
			drawn = true;
		}
	}

	@Override
	public void render() {
		renderObject.render(0, 0, 1, 1);
	}

	@Override
	public void onIntersect(Static s) {
	}
}