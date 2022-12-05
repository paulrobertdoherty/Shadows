package com.shadows;

import java.awt.image.*;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;

import com.shadows.entity.robot.*;
import com.shadows.entity.specific.*;
import com.shadows.level.*;
import com.shadows.networking.*;
import com.shadows.openGL.graphics.Color;

public class FileLoader {
	private Map<String, String> properties = new HashMap<String, String>();
	private FileLoader buttonText = null;
	public boolean fileHasContents = true;
	public static boolean loadedWorld = false;
	
	public FileLoader(String file, boolean dat) throws FileNotFoundException, IOException {
		if (!dat) {
			BufferedReader reader = new BufferedReader(new FileReader(new File(Shadows.directory + file)));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] twoS = line.split(":");
				properties.put(twoS[0], twoS[1]);
			}
			reader.close();
		} else {
			buttonText = new FileLoader("/res/world_rotated_right_90_degrees.txt", false);
			constructorSave(Shadows.directory, file);
		}
	}
	
	private byte[] getBytes(File file) throws FileNotFoundException, IOException{
		 RandomAccessFile f = new RandomAccessFile(file, "r");
	     try {
		     // Get and check length
		     long longlength = f.length();
		     int length = (int) longlength;
		     if (length != longlength) {
		    	 throw new IOException("File size >= 2 GB");
		     }
		     // Read file and return data
		     byte[] data = new byte[length];
		     f.readFully(data);
		     return data;
	     } finally {
	    	 f.close();
	     }
	}
	
	private void constructorSave(String directory, String file) throws IOException {
		boolean[][] existArray = null;
		
		File fileLoc = new File(directory.substring(0, directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves/" + file + ".dat");
		if (fileLoc.exists() && !fileLoc.isDirectory()) {
			byte[] output = getBytes(fileLoc);
			
			LevelSave save = null;
			
			try {
				save = (LevelSave)UDPClient.deserialize(output);
			} catch (ClassNotFoundException e) {
				System.err.println(".DAT file not recognized properly.");
				e.printStackTrace();
				return;
			}
			
			Shadows.cameraLocation.x = save.getPlayerX();
			Shadows.cameraLocation.z = save.getPlayerZ();
		//	MainPlayer.quotaScore = save.getQuotaScore();
			
			Level.boxes = new ArrayList<Box>();
			/*
			Level.boxArray = getBoxArray(save.getBoxArray(), Level.boxes);
			Level.wallArray = getWallArray(save.getWallArray());
			*/
			Level.totalBoxCount = save.getAmount();
			MainPlayer.boxesCollected = save.getCollected();
			Robot.robots = getRobotList(save.getRobotLocationList());
			
			existArray = save.getBoxExistsArray();
			
			/*
			if (save.getSecondsLeft() > 0) {
				
				//Shadows.timeLeft = save.getSecondsLeft();
			} else {
				Level.boxes = new ArrayList<Box>();
				Level.boxArray = null;
				Level.wallArray = null;
				Level.totalBoxCount = 0;
				MainPlayer.boxesCollected = 0;
				//Shadows.timeLeft = 0;
			
				Shadows.l = new Level(128, 96);
			}
			*/
		} else {
			fileHasContents = false;
			Robot.robots.clear();
			MainPlayer.boxesCollected = 0;
			Level.totalBoxCount = 0;
			Shadows.cameraLocation.x = 1;
			Shadows.cameraLocation.z = 1;
			Level.boxes.clear();
			Level.walls.clear();
			loadedWorld = false;
		}
		Level.boxArray = new Box[128][128];
		Level.wallArray = new Wall[128][128];
		Level.buttonArray = new Button[128][128][];
		loadLevelFromImage(Shadows.directory + "/res/world_rotated_right_90_degrees.png", existArray);
	}

	private List<Robot> getRobotList(List<RobotSave> robotLocationList) {
		List<Robot> robotList = new ArrayList<Robot>();
		for (RobotSave r : robotLocationList) {
			BasicRobot robot = new BasicRobot(r.getX(), 0, r.getZ(), 1, 1, 1, null, r.getColor());
			robot.draw();
			robotList.add(robot);
		}
		return robotList;
	}
	
	public static List<RobotSave> getRobotLocationList(List<Robot> robots) {
		List<RobotSave> robotLocationList = new ArrayList<RobotSave>();
		for (Robot r : robots) {
			BasicRobot bR = (BasicRobot)r;
			robotLocationList.add(new RobotSave(r.getX(), r.getZ(), bR.getColor()));
		}
		return robotLocationList;
	}
	
	public static boolean[][] getExistArray(Box[][] array) {
		boolean[][] returnArray = new boolean[128][128];
		for (int x = 0; x < 128; x++) {
			for (int z = 0; z < 128; z++) {
				if (array[x][z] != null && array[x][z].exists()) {
					returnArray[x][z] = true;
				}
			}
		}
		return returnArray;
	}
	
	public void placeButton(String key, int x, int y) {
		String property = buttonText.getProperty(key);
		int targetX = Integer.parseInt(property.split(", ")[0]);
		int targetZ = Integer.parseInt(property.split(", ")[1]);
		Button button = new Button(x, 0, y, 1, 1, 0.5f, Shadows.box, targetX, targetZ);
		Level.buttons.add(button);
		
		//Add button to array
		if (Level.buttonArray[x][y] != null) {
			List<Button> bList = Arrays.asList(Level.buttonArray[x][y]);
			bList.add(button);
			Button[] bArray = new Button[bList.size()];
			Level.buttonArray[x][y] = bList.toArray(bArray);
		} else {
			List<Button> bList = new ArrayList<Button>();
			bList.add(button);
			Button[] bArray = new Button[bList.size()];
			Level.buttonArray[x][y] = bList.toArray(bArray);
		}
	}
	int count = 1;
	
	public void testIfPlaceButton(int x, int y) {
		String key = x + ", " + y + "_" + count;
		if (buttonText.isProperty(key)) {
			placeButton(key, x, y);
			count++;
			if (buttonText.isProperty(key.substring(key.length() - Integer.toString(count).length()))) {
				testIfPlaceButton(x, y);
			} else {
				count = 1;
			}
		}
	}
	
	public void loadLevelFromImage(String fileName, boolean[][] existArray) {
		if (!loadedWorld) {
			try {
				BufferedImage image = ImageIO.read(new File(fileName));
				
				for (int x = 0; x < image.getWidth(); x++) {
					for (int y = 0; y < image.getHeight(); y++) {
						Color color = new Color(new java.awt.Color(image.getRGB(x, y), false));
						if (Robot.robots.size() < 4) {
							testIfPlaceRobot(color, x, y);
						}
						testIfPlaceBlock(color, x, y, existArray);
						
						testIfPlaceButton(x, y);
						
						//tests if the game should place a wall
						if (color.matches(0, 0, 0, 255)) {
							Wall wall = new Wall(x, 0, y, 1, 2f, 1, Shadows.box);
							Level.walls.add(wall);
							Level.wallArray[x][y] = wall;
						}
					}
				}
				loadedWorld = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void createBlock(int x, int z, Color color) {
		Box entityBox = new Box(x, 0, z, 1, 1, 1, Shadows.box, color);
		entityBox.setExists(true);
		Level.boxes.add(entityBox);
		Level.boxArray[x][z] = entityBox;
		if (color.matches(1, 0, 1, 1)) {
			Level.totalBoxCount++;
		}
	}
	
	private static void testIfPlaceBlock(Color color, int x, int y, boolean[][] existArray) {
		if (existArray == null || existArray[x][y]) {
			if (color.matches(255, 0, 0, 255)) {
				createBlock(x, y, new Color(1, 0, 0, 1));
			} else if (color.matches(0, 255, 0, 255)) {
				createBlock(x, y, new  Color(0, 1, 0, 1));
			} else if (color.matches(0, 0, 255, 255)) {
				createBlock(x, y, new Color(0, 0, 1, 1));
			} else if (color.matches(255, 255, 0, 255)) {
				createBlock(x, y, new Color(1, 1, 0, 1));
			} else if (color.matches(255, 0, 255, 255)) {
				createBlock(x, y, new Color(1, 0, 1, 1));
			}
		}
	}

	private static void testIfPlaceRobot(Color color, int x, int y) {
		if (color.matches(0, 0, 128, 255)) {
			Robot.createRobot(x, y, new Color(0, 0, 1, 1));
		} else if (color.matches(128, 0, 0, 255)) {
			Robot.createRobot(x, y, new Color(1, 0, 0, 1));
		} else if (color.matches(0, 128, 0, 255)) {
			Robot.createRobot(x, y, new Color(0, 1, 0, 1));
		} else if (color.matches(128, 128, 0, 255)) {
			Robot.createRobot(x, y, new Color(1, 1, 0, 1));
		}
	}
	
	public static boolean[][] getBoxArray(Box[][] boxArray) {
		boolean[][] array = new boolean[128][128];
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				if (boxArray[i][j] != null) {
					array[i][j] = true;
				}
			}
		}
		return array;
	}
	
	public static boolean[][] getWallArray(Wall[][] wallArray) {
		boolean[][] array = new boolean[128][128];
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 128; j++) {
				if (wallArray[i][j] != null) {
					array[i][j] = true;
				}
			}
		}
		return array;
	}
	
	public boolean fileHasContents() {
		return fileHasContents;
	}
	
	public boolean isProperty(String property) {
		return properties.get(property) != null;
	}

	public String getProperty(String key) {
		return properties.get(key);
	}
}