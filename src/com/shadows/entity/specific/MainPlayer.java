package com.shadows.entity.specific;

import java.io.*;
import java.net.*;

import org.lwjgl.input.*;
import org.lwjgl.util.vector.Vector2f;

import com.shadows.*;
import com.shadows.collision.*;
import com.shadows.entity.*;
import com.shadows.entity.robot.BasicRobot;
import com.shadows.entity.robot.Robot;
import com.shadows.level.*;
import com.shadows.networking.*;
import com.shadows.openGL.drawer.*;
import com.shadows.sound.*;

public class MainPlayer extends Player {
	public static boolean isWalking = true, saved = false;
	public static WalkableTerrain terrain = null;
	public static int boxesCollected = 0;
	public static String lastHit = null;
	public static Sound hitSound = null;
	public static int quotaScore = 0;
	private long lastSent = 0l;
	private long lastFired = 0l;
	private static Sound sound = null, checkPointSound = null;
	private static boolean firstDied = false;

	public MainPlayer(float x, float y, float z, float length, float width, float height, RenderObject renderObject) {
		super(x, y, z, length, width, height, renderObject);
		if (sound == null) {
			try {
				sound = new Sound("/res/sounds/Box.wav");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		if (hitSound == null) {
			try {
				hitSound = new Sound("/res/sounds/hit.wav");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
		if (checkPointSound == null) {
			try {
				checkPointSound = new Sound("/res/sounds/checkpoint.wav");
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}
	
	int secondsPassed = (int) (Shadows.currentTime / 1000);
	int collectedBlocks = 0;
	int actualLowestLeft = 170;
	String quotaString = null;
	
	public void update(int delta) {
		if (!isWalking) {
			this.x = Shadows.cameraLocation.x;
			this.y = Shadows.cameraLocation.y;
			this.z = Shadows.cameraLocation.z;
		} else {
			this.x = Shadows.cameraLocation.x;
			this.z = Shadows.cameraLocation.z;
			this.y = Shadows.cameraLocation.y = 13;
			
			/*
			if (Level.boxArray != null || !Shadows.connected) {
				if (Shadows.writer != null) {
					if (!CenterText.messageBoxShowed()) {
						if (Shadows.connected) {
							CenterText.createMessageBox();
						} else {
							//Load and save to save.txt
							int actualTotalBoxCount = (int)((float)(Level.totalBoxCount) * (170.0f / (float)(Level.totalBoxCount + boxesCollected)));
							
							if (!Shadows.resultsShown) {
								try {
									secondsPassed = (int) (Shadows.currentTime / 1000);
									collectedBlocks = 0;
									
									if (Shadows.loader.fileHasContents()) {
										actualLowestLeft = collectedBlocks = Integer.parseInt(Shadows.loader.getProperty("amount"));
									}
									float percentOfChange = 100.0f - (((float)(actualTotalBoxCount) / (float)(actualLowestLeft)) * 100.0f);
									quotaString = getQuota(percentOfChange);
									
									//get specific quota change for each quota
									if (quotaString.contentEquals("UNSATISFACTORY")) {
										if (quotaScore - 2 >= 0) {
											quotaString = "SATISFACTORY";
										}
									} else if (quotaString.contentEquals("FAILED")) {
										if (quotaScore - 10 >= 0) {
											quotaString = "AMAZING";
										}
									}
									quotaScore += (int)(percentOfChange / 70.0f) * 10;
									if (quotaScore < 0) {
										quotaScore = 0;
									}
									Insert code about getting npcs for your quota score
									
									if (collectedBlocks == 0 || actualTotalBoxCount < collectedBlocks) {
										collectedBlocks = actualTotalBoxCount;
									}
									Shadows.writer.writeObject(new LevelSave(null, null, null, 1, 1, 0, boxesCollected, collectedBlocks, 0, quotaScore));
									Shadows.writer.flush();
								} catch (FileNotFoundException e) {
									e.printStackTrace();
								} catch (IOException e) {
									e.printStackTrace();
								}
								Shadows.resultsShown = true;
							}
							
							//Write text to appear
							StringBuilder text = new StringBuilder("GAME REPORT");
							text.append(":n:");
							text.append("Boxes Left: ");
							text.append(actualTotalBoxCount);
							text.append(":n:");
							text.append("Least blocks left: ");
							text.append(actualLowestLeft);
							text.append(":n:");
							text.append("Quota Score: ");
							text.append(quotaScore);
							text.append(":n:");
							text.append("Final Quota: ");
							text.append(":n:");
							text.append(":c:");
							text.append(quotaString);
							CenterText.setMessage(text.toString(), true);
							saved = true;
						}
					}
				}
			}
			*/
		}
		
		testFire();
		
		if (System.currentTimeMillis() - lastSent > 100) {
			try {
				sendPacket(false);
			} catch (IOException e){
				e.printStackTrace();
			}
			lastSent = System.currentTimeMillis();
		}
		
		/*
		collision.setX(x - 0.5f);
		collision.setY(y);
		collision.setZ(z - 0.5f);
		*/
	}
	
	/*
	private String getQuota(float diff) {
		String quota = "INDIFFERENT";
		if (diff < 0) {
			quota = "UNSATISFACTORY";
			if (diff <= -70) {
				quota = "FAILED";
			}
		} else if (diff > 0) {
			quota = "SATISFACTORY";
			if (diff >= 70) {
				quota = "AMAZING";
			}
		}
		return quota;
	}
	*/

	private void testFire() {
		if (Mouse.isButtonDown(0) && Shadows.connected && Shadows.health > 0) {
			if (System.currentTimeMillis() - lastFired > 100) {
				if (Shadows.connected) {
					try {
						sendPacket(true);
					} catch (IOException e){
						e.printStackTrace();
					}
				}
				launchProj(Shadows.shootSpeed, Shadows.cameraRotation.x, Shadows.cameraRotation.y, this.x, this.y, this.z, Shadows.name);
				
				lastFired = System.currentTimeMillis();
			}
		}
	}
	
	public static void launchProj(float velocity, float xRot, float yRot, float x, float y, float z, String name) {
		float xVel = 0;
		float yVel = 0;
		float zVel = 0;
		
		float adjacentZ = (float) (Math.cos(Math.toRadians(yRot)) * velocity);
		float oppositeZ = (float) (Math.sin(Math.toRadians(yRot)) * velocity);
		zVel -= adjacentZ;
		xVel += oppositeZ;
		
		float oppositeY = (float) (Math.sin(Math.toRadians(xRot)) * velocity);
		yVel -= oppositeY;
		
		Shadows.fireProjectile(x - 0.0625f, y - 0.0625f, z, xVel, yVel, zVel, name);
	}
	
	private void sendPacket(boolean fire) throws IOException {
		if (Shadows.connected) {
			PlayerPacket packet = null;
			if (fire) {
				if (Shadows.isServer) {
					packet = new FirePlayerPacket(this.x, this.y, this.z, Shadows.name, Shadows.cameraRotation.x, Shadows.cameraRotation.y, Shadows.cameraRotation.z);
				} else {
					packet = new FirePlayerPacket(this.x, this.y, this.z, Shadows.name, Shadows.cameraRotation.x, Shadows.cameraRotation.y, Shadows.cameraRotation.z);
				}
			} else {
				if (Shadows.isServer) {
					packet = new PlayerPacket(this.x, this.y, this.z, Shadows.name);
				} else {
					packet = new PlayerPacket(this.x, this.y, this.z, Shadows.name);
				}
			}
			
			if (Shadows.isServer) {
				if (Shadows.health > 0) {
					UDPServer.sendPacket(packet);
				} else if (!firstDied) {
					TCPServer.sendMessage(new DiedPacket(Shadows.name, lastHit, boxesCollected));
					boxesCollected = 0;
					firstDied = true;
				}
			} else {
				if (Shadows.health > 0) {
					UDPClient.sendPacket(packet, ChatThread.ip);
				} else if (!firstDied) {
					TCPClient.sendMessage(new DiedPacket(Shadows.name, lastHit, boxesCollected));
					boxesCollected = 0;
					firstDied = true;
				}
			}
		}
	}

	public static void testCollision(float x, float y, float z, Static s) {
		BasicRobot bR = (BasicRobot)s;
		
		Box collision = getCollision(x, y, z, bR);
		if (collision != null && collision.exists()) {
			
			//Perform result based on what type of block it is, whether checkpoint or regular
			if (bR.getColor().matches(collision.getColor())) {
				bR.setX(collision.getX());
				bR.setZ(collision.getZ());
				bR.setInCheckpoint(true);
				bR.lastCheckpoint = new Vector2f(collision.getX(), collision.getZ());
				removeBlock(x, z, collision);
				if (bR.checkPointX != collision.getX() && bR.checkPointZ != collision.getZ()) {
					checkPointSound.play();
				}
			} else if (bR.getColor().matches(1, 0, 1, 1)) {
				boxesCollected++;
				removeBlock(x, z, collision);
				collision.onIntersect(s);
				bR.setInCheckpoint(false);
			}
		} else {
			int robotsInCheckpoint = 0;
			int robotsInOldCheckpoint = 0;
			for (Robot r : Robot.robots) {
				if (r.inCheckpoint && !(r.getX() == r.checkPointX && r.getZ() == r.checkPointZ)) {
					robotsInCheckpoint++;
				}
				if (r.getX() == r.checkPointX && r.getZ() == r.checkPointZ) {
					robotsInOldCheckpoint++;
				}
			}
			
			if (robotsInCheckpoint == 4) {
				for (Robot r : Robot.robots) {
					BasicRobot rr = (BasicRobot)r;
					rr.setCheckpoint((int)rr.getX(), (int)rr.getZ());
					rr.setInCheckpoint(false);
				}
			} else if (robotsInOldCheckpoint == 4) {
				for (Robot r : Robot.robots) {
					BasicRobot rr = (BasicRobot)r;
					rr.setInCheckpoint(false);
				}
			}
		}
	}
	
	private static void removeBlock(float x, float y, Box collision) {
		collision.setExists(false);
		Level.boxes.remove(collision);
		if (collision.getColor().matches(1, 0, 1, 1)) {
			Level.totalBoxCount--;
		}
	}

	public static Box getCollision(float x, float y, float z, BasicRobot s) {
		if (x < Level.boxArray.length 
			&& z < Level.boxArray.length && 
			x > 0 && z > 0 && y < 1 && y >= 0) {
			return getEither(Level.boxArray[(int)x][(int)z], Level.boxArray[(int)x + 1][(int)z], Level.boxArray[(int)x][(int)z + 1], Level.boxArray[(int)x + 1][(int)z + 1], s);//getABoxAroundACoordinate((int)x, (int)z);
		}
		return null;
	}
	
	private static Box getEither(Box a, Box b, Box c, Box d, BasicRobot s) {
		Box returner = null;
		if (a != null) {
			returner = a;
			if (a.getColor().matches(s.getColor())) {
				return returner;
			}
		}
		if (b != null) {
			returner = b;
			if (b.getColor().matches(s.getColor())) {
				return returner;
			}
		}
		if (c != null) {
			returner = c;
			if (c.getColor().matches(s.getColor())) {
				return returner;
			}
		}
		if (d != null) {
			returner = d;
		}
		
		return returner;
	}

	@Override
	public void draw() {
		renderObject.draw();
	}

	@Override
	public void render() {
		renderObject.render(0, 0, 1, 1);
	}

	@Override
	public void onIntersect(Static s) {
		if (s instanceof Projectile) {
			Projectile p = (Projectile)s;
			Shadows.health--;
			playSound();
			lastHit = p.getName();
		}
	}

	public static void playSound() {
		sound.play();
	}
}