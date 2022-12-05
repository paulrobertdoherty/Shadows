package com.shadows.openGL.graphics.gui;

import java.io.*;
import java.util.*;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.shadows.Buffer;
import com.shadows.Shadows;
import com.shadows.networking.*;
import com.shadows.openGL.*;
import com.shadows.openGL.graphics.*;

import static org.lwjgl.opengl.GL11.*;

public class MainMenu {
	public static boolean canRun = true;
	public static MenuState currentState = MenuState.MENU;
	public static int halfWidth = 0;
	public static int halfHeight = 0;
	
	public static Texture buttonTexture = null;
	public static Texture textBoxTexture = null;
	public static Button host = null, join = null, play = null, newSave = null, back = null, back2 = null, back3 = null, back4 = null, select = null, delete = null, yes = null, no = null, rename = null;
	public static TextBox textBox = null, saveName = null;
	public static List<String> saves = null;
	public static String save = null;
	private static boolean isRenaming = false;
	
	public static void start() {
		GUIObject.draw();
		GUIObject.ready2D();
		halfWidth = Display.getWidth() / 2;
		halfHeight = Display.getHeight() / 2;
		buttonTexture = new Texture("/res/textures/button.png");
		textBoxTexture = new Texture("/res/textures/textbox.png");
		host = new Button(new HostEvent(), "Host");
		join = new Button(new JoinEvent(), "Join");
		play = new Button(new PlayEvent(), "Play");
		newSave = new Button(new SaveEvent(), "Create New Save");
		delete = new Button(new DeleteEvent(), "Delete");
		rename = new Button(new RenameEvent(), "Rename");
		select = new Button(new SelectEvent(), "Play Selected");
		back = new Button(new BackEvent(), "Back");
		back2 = new Button(new Back2Event(), "Back");
		yes = new Button(new YesEvent(), "Yes");
		no = new Button(new NoEvent(), "No");
		
		textBox = new TextBox(halfWidth - 112, halfHeight);
		saveName = new TextBox(halfWidth - 112, halfHeight);
		glLight(GL_LIGHT0, GL_POSITION, Buffer.asFloatBuffer(new float[] {
				0, 0, 0, 1
		}));
		glLight(GL_LIGHT0, GL_AMBIENT, Buffer.asFloatBuffer(new float[] {
				1f, 1f, 1f, 1
		}));
		OpenGL.readyPerspective();
	}
	
	public static void draw() {
		OpenGL.readyOrtho();
		halfWidth = Display.getWidth() / 2;
		halfHeight = Display.getHeight() / 2;
		switch (currentState) {
			case MENU:
				host.setBounds(halfWidth - 64, halfHeight + 70, 128, 64);
				join.setBounds(halfWidth - 64, halfHeight, 128, 64);
				play.setBounds(halfWidth - 64, halfHeight - 70, 128, 64);
				
				host.render(buttonTexture, false);
				join.render(buttonTexture, false);
				play.render(buttonTexture, false);
				String title = "An Exploration of Pointlessness";
				Text.drawText(halfWidth - (title.length() * 10), halfHeight + 134, 20, 20, title);
				glBindTexture(GL_TEXTURE_2D, 0);
				//host.testClick();
				//join.testClick();
				play.testClick();
				break;
			
			case JOIN:
				Text.drawText(halfWidth - 112, halfHeight + 16, 16, 16, "Enter IP Here:");
				textBoxStuff();
				break;
			case WAIT:
					if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
						currentState = MenuState.MENU;
					}
					if (Shadows.connected) {
						Shadows.mainMenu = false;
						Shadows.gameInstance.setUpGame();
						break;
					}
					String status = Chat.getCurrentConnectionStatus();
					int textSize = 16;
					Text.drawText(halfWidth - ((status.length() * textSize) / 2), halfHeight - textSize / 2, textSize, textSize, status);
					break;
			case CHOOSE:
				//Handle buttons
				newSave.setBounds(halfWidth - 128, halfHeight / 3, 256, 64);
				newSave.render(buttonTexture, false);
				newSave.testClick();
				
				delete.setBounds(halfWidth - 268, halfHeight / 3 - 70, 128, 64);
				delete.render(buttonTexture, false);
				delete.testClick();
				
				rename.setBounds(halfWidth - 268, halfHeight / 3, 128, 64);
				rename.render(buttonTexture, false);
				rename.testClick();
				
				select.setBounds(halfWidth - 128, halfHeight / 3 - 70, 256, 64);
				select.render(buttonTexture, false);
				select.testClick();
				
				back2.setBounds(halfWidth + 140, halfHeight / 3 - 70, 128, 128);
				back2.render(buttonTexture, false);
				back2.testClick();
				
				ScreenList list = new ScreenList(Display.getWidth() / 4, Display.getHeight() / 3, Display.getWidth() / 2, (Display.getHeight() / 3) * 2, getSaves());
				String returned = list.renderAndTest();
				if (returned != null) {
					save = returned;
				}
				break;
			case SAY:
				saveName.setBounds(halfWidth - 112, halfHeight);
				Text.drawText(halfWidth, halfHeight + 32, 16, 16, "Press Return to:n:enter save name");
				saveName.render(textBoxTexture);
				saveName.testClick();
				
				back.setBounds(halfWidth - 64, (halfHeight / 3) - 64, 128, 64);
				back.render(buttonTexture, false);
				back.testClick();
				
				String text = saveName.getText();
				if (!text.contentEquals("")) {
					if (isRenaming) {
						File file = new File(System.getProperty("user.dir").substring(0, Shadows.directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves/" + save + ".dat");
						file.renameTo(new File(System.getProperty("user.dir").substring(0, Shadows.directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves/" + text + ".dat"));
						isRenaming = false;
						currentState = MenuState.CHOOSE;
					} else {
						Shadows.mainMenu = false;
						Shadows.lastSaveFile = text;
						Shadows.gameInstance.setUpGame();
						try {
							Shadows.setUpSaveFile(false, false, text);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
						currentState = MenuState.MENU;
					}
					saves = null;
					save = null;
					saveName.setText("");
				}
				break;
			case QUESTION:
				String toSay1 = "Are you sure you want to";
				String toSay3 = "delete the file?";
				String toSay2 = "It will not be recoverable.";
				Text.drawText(halfWidth + (toSay1.length() / 2), halfHeight + 48, 16, 16, toSay1 + ":n:" + toSay3 + ":n:" + toSay2);
				
				yes.setBounds(halfWidth - 128, halfHeight - 48, 128, 64);
				yes.render(buttonTexture, false);
				yes.testClick();
				
				no.setBounds(halfWidth + 30, halfHeight - 48, 128, 64);
				no.render(buttonTexture, false);
				no.testClick();
				
				break;
	}
		OpenGL.readyPerspective();
	}
	
	private static java.util.List<String> getSaves() {
		if (saves == null) {
			saves = new ArrayList<String>();
			File savesDirectory = new File(System.getProperty("user.dir").substring(0, Shadows.directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves");
			
			if (savesDirectory.exists() && savesDirectory.isDirectory()) {
				List<File> savesList = Arrays.asList(savesDirectory.listFiles());
				Iterator<File> saveIterator = savesList.iterator();
				while (saveIterator.hasNext()) {
					File save = saveIterator.next();
					if (!save.getName().equals(".DS_Store")) {
						saves.add(save.getName().substring(0, save.getName().lastIndexOf('.')));
					}
				}
			}
		}
		return saves;
	}

	private static void textBoxStuff() {
		if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			currentState = MenuState.MENU;
		}
		
		textBox.setBounds(halfWidth - 112, halfHeight);
		
		textBox.render(textBoxTexture);
		glBindTexture(GL_TEXTURE_2D, 0);
		textBox.testClick();
		String text = textBox.getText();
		if (!text.contentEquals("")) {
			Shadows.isServer = false;
			if (text.contains(":")) {
				ChatThread.ip = text.split(":")[0];
				ChatThread.port = Integer.parseInt(text.split(":")[1]);
			} else {
				ChatThread.ip = text;
			}
			Shadows.gameInstance.chatThread.start();
			currentState = MenuState.WAIT;
			textBox.setText("");
		}
	}
	
	public static class HostEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.WAIT;
			Shadows.isServer = true;
			Shadows.gameInstance.chatThread.start();
		}
	}
	
	public static class JoinEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.JOIN;
		}
	}
	
	public static class PlayEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.CHOOSE;
		}
	}
	
	public static class SaveEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.SAY;
		}
	}
	
	public static class BackEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.CHOOSE;
		}
	}
	
	public static class SelectEvent implements ButtonEvent {
		@Override
		public void onClick() {
			if (save != null) {
				Shadows.mainMenu = false;
				Shadows.lastSaveFile = save;
				Shadows.gameInstance.setUpGame();
				try {
					Shadows.setUpSaveFile(false, false, save);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				currentState = MenuState.MENU;
				saves = null;
			}
		}
	}
	
	public static class DeleteEvent implements ButtonEvent {
		@Override
		public void onClick() {
			if (save != null) {
				currentState = MenuState.QUESTION;
			}
		}
	}
	
	public static class YesEvent implements ButtonEvent {
		@Override
		public void onClick() {
			File file = new File(System.getProperty("user.dir").substring(0, Shadows.directory.length() - (new String("Shadows").length() + 1)) + "/Shadows_Saves/" + save + ".dat");
			file.delete();
			save = null;
			saves = null;
			currentState = MenuState.CHOOSE;
		}
	}
	
	public static class NoEvent implements ButtonEvent {
		@Override
		public void onClick() {
			currentState = MenuState.CHOOSE;
		}
	}
	
	public static class RenameEvent implements ButtonEvent {
		@Override
		public void onClick() {
			if (saves != null) {
				currentState = MenuState.SAY;
				isRenaming = true;
			}
		}
	}
	
	public static class Back2Event implements ButtonEvent {
		public void onClick() {
			currentState = MenuState.MENU;
			saves = null;
			save = null;
		}
	}
}