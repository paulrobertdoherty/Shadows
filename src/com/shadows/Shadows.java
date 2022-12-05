package com.shadows;

import java.awt.Canvas;
import java.io.*;
import java.net.*;
import java.util.*;

import org.lwjgl.*;
import org.lwjgl.input.*;
import org.lwjgl.opengl.*;
import org.lwjgl.util.vector.*;

import com.shadows.entity.robot.*;
import com.shadows.entity.specific.*;
import com.shadows.entity.specific.Button;
import com.shadows.level.*;
import com.shadows.model.*;
import com.shadows.networking.*;
import com.shadows.openGL.*;
import com.shadows.openGL.drawer.*;
import com.shadows.openGL.graphics.*;
import com.shadows.openGL.graphics.gui.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL20.*;

public class Shadows implements Runnable{
	public static Shadows gameInstance = new Shadows();
	public Thread renderThread = new Thread(this);
	public Thread chatThread = new Thread(new ChatThread());
	public Thread locationThread = new Thread(new LocationThread());
	public static boolean canRun = true, isServer = false, connected = false, mainMenu = true, pauseMenu = false, resultsShown;
	public static float fov, zNear, zFar;
	public static float cameraSpeed = 0.005f, shootSpeed = 0.009f;;
	public static Vector3f cameraLocation = new Vector3f(0, 0f, 0);
	public static Vector3f cameraRotation = new Vector3f(0, 135, 0);
	public static int viewDistence = 20;
	public static Texture terrainTexture = null;
	public static int shader = 0;
	public static Model model = null, box = null;
	public static Camera camera = new Camera();
	public static MainPlayer player = null;
	public static Floor floor = null;
	public static Game g = null;
	public static Level l = null;
	public static String fps = "", name = "", directory = "", lastSaveFile = "";
	public static int maxHealth = 10;
	public static int health = 10;
	public static long startTime = 0l, currentTime = 0l, timeLeft = 0, lastSave;
	public static FileLoader loader = null;
	public static FileWriter writer = null;
	public static com.shadows.openGL.graphics.gui.Button back = null, quit = null;
	
	Canvas canvas;
	
	//Insert parameters here
	/**
	 * Starts the Shadows engine
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length > 0) {
			name = "Default Name";//args[0];
			directory = args[0];
		}
		gameInstance.startThreads();
	}
	
	public void startThreads() {
		renderThread.setPriority(Thread.MAX_PRIORITY);
		renderThread.start();
	}

	public void run() {
		zNear = 0.3f;
		zFar = viewDistence;
		fov = 30f;
		
		new Screen(640, 480).createWindow();
		System.out.println("Display was successfully created!");
		
		try {
			Mouse.create();
		} catch (LWJGLException e1) {
			System.err.println("Mouse failed to create.");
			e1.printStackTrace();
			Display.destroy();
			System.exit(1);
		}
		System.out.println("Mouse was successfully created!");
		
		OpenGL.setUp();
		OpenGL.setUpFog(viewDistence - 6, 5, 0.005f);
		OpenGL.setUpShadowMaps();
		Text.setUp("/res/textures/font.png", 16);
		MainMenu.start();
		
		g = new Game();
		g.getDelta();
		g.start();
		
		Screen.canRender = true;
		while (canRun) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			
			if (Display.isCloseRequested()) {
				canRun = false;
				connected = false;
			}
			
			if (Keyboard.isKeyDown(Keyboard.KEY_F11)) {
				try {
					if (!Display.isFullscreen()) {
						Display.setDisplayModeAndFullscreen(Display.getDesktopDisplayMode());
						int width = Display.getDisplayMode().getWidth();
						int height = Display.getDisplayMode().getHeight();
						glViewport(0, 0, width, height);
					} else {
						Display.setDisplayMode(new DisplayMode(640, 480));
						glViewport(0, 0, 640, 480);
					}
				} catch (LWJGLException e) {
					e.printStackTrace();
				}
			}
			
			KeyboardHandler.update();
			
			if (mainMenu) {
				MainMenu.draw();
			} else {
				if (System.currentTimeMillis() - lastSave >= 5000l && !pauseMenu && !CenterText.messageShowing() && !MainPlayer.saved) {
					saveLevel();
					lastSave = System.currentTimeMillis();
				}
				
				glPushAttrib(GL_ALL_ATTRIB_BITS);
				ShadowMapper.generateTextureCoords(false);
				
				ShadowMapper.drawShadowMap(cameraLocation.x, 1, cameraLocation.z);
				glUseProgram(shader);
				drawObjects(false);
				
				glPopAttrib();
			}
			
			glUniform1i(glGetUniformLocation(shader, "state"), 2);
			drawHUD();
			if (!mainMenu) {
				moveCamera();
			}
			
			update();
		}
		forceQuit(false);
	}
	
	private void drawHUD() {
		OpenGL.readyOrtho();
		GUIObject.ready2D();
		glDisable(GL_DEPTH_TEST);
		
		int textSize = (int)(16.0f * ((float)Display.getHeight() / 480.0f));
		
		if (!mainMenu) {
			Text.drawText(0, Display.getHeight() - textSize, textSize, textSize, fps);
			
			//Reset from game report
			if (!CenterText.messageShowing() && MainPlayer.saved) {
				MainPlayer.boxesCollected = 0;
				Shadows.startTime = 0;
				Shadows.currentTime = 0;
				Shadows.loader = null;
				Shadows.writer = null;
				Level.boxes = new ArrayList<Box>();
				try {
					Shadows.setUpSaveFile(false, false, Shadows.lastSaveFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
				MainPlayer.saved = false;
			}
			
			if (timeLeft <= -1) {
				startTime = 0;
				try {
					setUpSaveFile(true, true);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				CenterText.setShowed(false);
				Shadows.resultsShown = false;
			}
			
			//Draw countdown clock
			/*
			if (!Shadows.connected) {
				//Start the clock
				if (startTime == 0) {
					startTime = System.currentTimeMillis();
				}
				//Pause the clock
				if (!Mouse.isGrabbed()) {
					startTime = System.currentTimeMillis() - currentTime;
				}
				currentTime = System.currentTimeMillis() - startTime;
				
				int minutes = 5;
				int timeLeft = (minutes * 60) - ((int)(currentTime) / 1000);
				
				timeLeft -= Shadows.timeLeft;
				
				if (timeLeft <= -1) {
					startTime = 0;
					try {
						setUpSaveFile(true, true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
					CenterText.setShowed(false);
					Shadows.resultsShown = false;
				}
				
				String toDisplay = timeLeft / 60 + ":" + timeLeft % 60;
				Text.drawText((Display.getWidth() / 2) - (toDisplay.length() * (textSize / 2)), Display.getHeight() - textSize, textSize, textSize, toDisplay);
			}
			*/
			
			String boxCount = MainPlayer.boxesCollected + "/" + Level.totalBoxCount;
			Text.drawText(Display.getWidth() - (boxCount.length() * textSize), Display.getHeight() - textSize, textSize, textSize, boxCount);
			
			Minimap.draw();
			if (connected) {
				Chat.drawChat();
				if (!KeyboardHandler.isTyping) {
					HealthBar.draw();
				}
				
				if (health > 0 && Level.boxes.size() > 0) {
					//Crosshair.draw();
				}
			}
			Crosshair.draw();
			Chat.updateESC();
			CenterText.draw();
			
			//Draw option menu
			if (pauseMenu && !CenterText.messageShowing()) {
				int halfWidth = Display.getWidth() / 2;
				int halfHeight = Display.getHeight() / 2;
				
				if (back == null) {
					back = new com.shadows.openGL.graphics.gui.Button(new BackEvent(), "Back");
					quit = new com.shadows.openGL.graphics.gui.Button(new QuitEvent(), "Save and Quit");
				} else {
					back.setBounds(halfWidth - 64, halfHeight + 35, 128, 64);
					quit.setBounds(halfWidth - 128, halfHeight - 35, 256, 64);
				}
				
				back.render(MainMenu.buttonTexture, true);
				quit.render(MainMenu.buttonTexture, true);
				
				back.testClick();
				quit.testClick();
			}
		} else {
			CenterText.draw();
		}
		
		glEnable(GL_DEPTH_TEST);
		OpenGL.readyPerspective();
		GUIObject.ready3D();
	}

	private void createLevel() {
		try {
			model = ModelLoader.loadOBJModel("/res/models/player.obj");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		
		try {
			box = ModelLoader.loadOBJModel("/res/models/box.obj");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		box.draw();
		Level.generateTerrain(128);
		player = new MainPlayer(0, 0, 0, 1, 1, 1, model);
	}
	
	private void createTexturesAndShaders() {
		terrainTexture = new Texture("/res/textures/Grass.png");
		try {
			if (shader == 0) {
				shader = Shader.loadShaderPair("/res/shaders/vertex.txt", "/res/shaders/fragment.txt");
			}
		} catch (LWJGLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		glUseProgram(shader);
		int regLoc = glGetUniformLocation(shader, "lookup");
		glUniform1i(regLoc, 0);
		
		int shadowLoc = glGetUniformLocation(shader, "shadow");
		glUniform1i(shadowLoc, 1);
		
		
	}
	
	private void moveCamera() {
		camera.processKeyboard();
		camera.processMouse(1, 90, -90);
		camera.move();
	}
	
	private void update() {
		Robot.testForRobotPlacement();
		
		g.updateFPS();
		g.getDelta();
		Display.update();
		if (Screen.vSyncEnabled) {
			Display.sync(60);
		}
	}
	
	private static void renderBoxes() {
		try {
			for (Box b : Level.boxes) {
				if (canRender(b.getX(), b.getY(), b.getZ())) {
					glPushMatrix();
					glTranslatef(b.getX(), b.getY(), b.getZ());
					glScalef(b.getLength(), b.getWidth(), b.getHeight());
					b.render();
					glPopMatrix();
				}
			}
		} catch (ConcurrentModificationException e) {
			System.err.println(e);
		}
	}

	private static void renderWalls() {
		for (Wall w : Level.walls) {
			if (canRender(w.getX(), w.getY(), w.getZ())) {
				glPushMatrix();
				glTranslatef(w.getX(), w.getY(), w.getZ());
				glScalef(w.getLength(), w.getWidth(), w.getHeight());
				w.render();
				glPopMatrix();
			}
		}
	}
	
	private static void renderButtons() {
		for (Button b : Level.buttons) {
			if (canRender(b.getX(), b.getY(), b.getZ())) {
				glPushMatrix();
				glTranslatef(b.getX(), b.getY(), b.getZ());
				glScalef(b.getLength(), b.getHeight(), b.getWidth());
				b.render();
				glPopMatrix();
			}
		}
	}

	public static void drawObjects(boolean shadow) {
		renderEntities(shadow);
		renderWalls();
		renderBoxes();
		renderButtons();
	}
	
	/**
	 * Quits the game without saving.
	 * @param error
	 */
	public static void forceQuit(boolean error) {
		if (connected) {
			connected = false;
			TCPClient.close();
			TCPServer.close();
		}
		Display.destroy();
		System.exit(error ? 1 : 0);
	}
	
	private static void renderEntities(boolean shadow) {
		if (!shadow) {
			//floor
			glUniform1i(glGetUniformLocation(shader, "state"), 1);
			glActiveTexture(GL_TEXTURE0);
			terrainTexture.bind();
			floor.render(1, 1, 1, 1);
			glBindTexture(GL_TEXTURE_2D, 0);
			glUniform1i(glGetUniformLocation(shader, "state"), 2);
			
			//outside walls
			glPushMatrix();
			glScalef(1, 16f / 128f, 1);
			glRotatef(270, 1, 0, 0);
			floor.render(0.5f, 0.5f, 0.5f, 1);
			glPopMatrix();
			
			glPushMatrix();
			glTranslatef(0, 0, 128);
			glScalef(1, 16f / 128f, 1);
			glRotatef(270, 1, 0, 0);
			floor.render(0.5f, 0.5f, 0.5f, 1);
			glPopMatrix();
			
			glPushMatrix();
			glScalef(1, 16f / 128f, 1);
			glRotatef(90, 0, 0, 1);
			floor.render(0.5f, 0.5f, 0.5f, 1);
			glPopMatrix();
			
			glPushMatrix();
			glTranslatef(128, 0, 0);
			glScalef(1, 16f / 128f, 1);
			glRotatef(90, 0, 0, 1);
			floor.render(0.5f, 0.5f, 0.5f, 1);
			glPopMatrix();
			
			glUniform1i(glGetUniformLocation(shader, "state"), 0);
		}
		/*
		
		for (String s : LocationThread.playerNames) {
			Player p = LocationThread.playerMap.get(s);
			if (canRender(p.getX(), p.getY(), p.getZ())) {
				glPushMatrix();
				glTranslatef(p.getX(), p.getY(), p.getZ());
				p.render();
				glPopMatrix();
			}
			p.update(g.delta);
		}
		Iterator<Projectile> it = LocationThread.projectiles.iterator();
		while (it.hasNext()) {
			Projectile p = it.next();
			if (canRender(p.getX(), p.getY(), p.getZ())) {
				glPushMatrix();
				glTranslatef(p.getX(), p.getY(), p.getZ());
				glScalef(p.getLength(), p.getWidth(), p.getHeight());
				p.render();
				glPopMatrix();
			}
			p.update(g.delta);
			if (LocationThread.projectilesToRemove.contains(p)) {
				it.remove();
			}
		}
		*/
		
		//do stuff with robots
		Robot.updateRobots(g.delta);
		Robot.renderRobots();
	}

	public void setUpGame() {
		lastSave = System.currentTimeMillis();
		createLevel();
		createTexturesAndShaders();
	}
	
	public static void sendLevel() {
		long seed = System.currentTimeMillis();
		int r = 32;
		l = new Level(128, seed, r);
		TCPServer.sendMessage(new LevelSettingPacket(seed, r));
	}

	public static void setLevel(long seed, int rareness) {
		l = new Level(128, seed, rareness);
	}

	public static void fireProjectile(float x, float y, float z, float xVel, float yVel, float zVel, String name) {
		Projectile p = new Projectile(x, y, z, 0.125f, 0.125f, 0.125f, box);
		p.setDX(xVel);
		p.setDY(yVel);
		p.setDZ(zVel);
		p.setName(name);
		LocationThread.projectiles.add(p);
	}
	
	public static boolean canRender(float x, float y, float z) {
		float diffX = x - cameraLocation.x;
		float diffY = y - cameraLocation.y;
		float diffZ = z - cameraLocation.z;
		
		if (Math.abs(diffX) < viewDistence) {
			if (Math.abs(diffY) < viewDistence) { 
				if (Math.abs(diffZ) < viewDistence) {
					/*
					float angleZ = fixAngle(getAngle(diffX, diffZ));
					
					float botY = cameraRotation.y - fov;
					float topY = cameraRotation.y + fov;
					
					if (angleZ > botY && angleZ < topY) {
						return true;
					}
					*/
					return true;
				}
			}
		}
		return false;
	}
	
	public static void setUpSaveFile(boolean write, boolean quickSaving, String save) throws FileNotFoundException, IOException {
		if (!write) {
			loader = new FileLoader(save, true);
		} else {
			if (!quickSaving) {
				save = checkForCopiesOfSaves(save);
			}
			writer = new FileWriter(save + ".dat");
		}
	}
	
	private static int timesRecursified = 0;
	
	private static String checkForCopiesOfSaves(String save) {
		if (MainMenu.saves != null && MainMenu.saves.contains(save)) {
			timesRecursified++;
			if (!MainMenu.saves.contains(save + " " + (timesRecursified + 1))) {
				return save + " " + (timesRecursified + 1);
			}
		}
		return save;
	}

	public static void setUpSaveFile(boolean write, boolean quickSave) throws FileNotFoundException, IOException {
		setUpSaveFile(write, quickSave, lastSaveFile);
	}
	
	public static class QuitEvent implements ButtonEvent {
		@Override
		public void onClick() {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			mainMenu = true;
			pauseMenu = false;
			
			saveLevel();
			Shadows.startTime = 0;
			Shadows.currentTime = 0;
			
		}
	}
	
	public static class BackEvent implements ButtonEvent {
		@Override
		public void onClick() {
			pauseMenu = false;
		}
	}
	
	public static void saveLevel() {
		new LevelSave(cameraLocation.x, cameraLocation.z, MainPlayer.boxesCollected, Level.totalBoxCount, FileLoader.getRobotLocationList(Robot.robots), FileLoader.getExistArray(Level.boxArray)).saveLevel(true);
	}

	/*
	private static float fixAngle(float angle) {
		float newAngle = angle + 90;
		if (newAngle > 0) {
			newAngle = 360 - (newAngle * -1);
		} else if (newAngle > 360) {
			newAngle -= 360;
		}
		return newAngle;
	}

	private static float getAngle(float x, float y) {
		float newX = x;
		float newY = y;
		if (x < 0) {
			newY *= -1;
		}
		if (y < 0) {
			newX *= -1;
		}
		return (float) (StrictMath.toDegrees(StrictMath.asin(newY / StrictMath.sqrt(newX * newX + newY * newY))));
	}
	*/
}