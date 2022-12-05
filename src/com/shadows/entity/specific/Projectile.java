package com.shadows.entity.specific;

import java.io.*;
import java.net.URISyntaxException;

import com.shadows.*;
import com.shadows.entity.*;
import com.shadows.networking.*;
import com.shadows.openGL.*;
import com.shadows.openGL.drawer.*;
import com.shadows.sound.*;

public class Projectile extends BaseEntity {
	private long startTime = 0l;
	private String name = "";
	private static boolean loadedSound = false;
	private static Sound sound = null;
	
	public Projectile(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
		if (!loadedSound) {
			try {
				sound = new Sound("/res/sounds/laser.wav");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			loadedSound = true;
		}
		sound.play();
		startTime = System.currentTimeMillis();
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void update(int delta) {
		super.update(delta);
		
		long timeAlive = System.currentTimeMillis() - startTime;
		boolean collidedWithWall = Camera.isColliding(this.x, this.y, this.z);
		boolean collidedWithMe = intersects(Shadows.player);
		for (String s : LocationThread.playerNames) {
			Player p = LocationThread.playerMap.get(s);
			if (intersects(p) && name.contentEquals(Shadows.name)) {
				MainPlayer.playSound();
				stop();
			}
		}
		if (collidedWithMe && !name.contentEquals(Shadows.name)) {
			Shadows.player.onIntersect(this);
			stop();
		}
		if (y < 0 || timeAlive >= 5000 || collidedWithWall) {
			stop();
		}
	}
	
	private void stop() {
		LocationThread.projectilesToRemove.add(this);
	}
	
	public String getName() {
		return name;
	}

	@Override
	public void draw() {
		
	}

	@Override
	public void render() {
		renderObject.render(1, 1, 0, 1);
	}

	@Override
	public void onIntersect(Static s) {
		
	}
}