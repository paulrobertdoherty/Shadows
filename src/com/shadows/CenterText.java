package com.shadows;

import org.lwjgl.input.*;
import org.lwjgl.opengl.*;

import com.shadows.networking.*;
import com.shadows.openGL.graphics.*;
import com.shadows.openGL.graphics.gui.*;

import static org.lwjgl.opengl.GL11.*;

public class CenterText {
	private static int halfWidth, halfHeight;
	private static boolean showMessageBox = false, showMessage = false, messageShown = false, showBackground;
	private static TextBox textBox = null;
	private static String message = null;
	private static int startX = -200;
	private static float x = -200;
	
	private static void setMessage(String message) {
		showMessage = true;
		CenterText.message = message;
	}
	
	public static void createMessageBox() {
		showMessageBox = true;
		messageShown = false;
	}
	
	public static void draw() {
		halfWidth = Display.getWidth() / 2;
		halfHeight = Display.getHeight() / 2;
		
		if (showMessageBox) {
			messageShown = true;
			Text.drawText(halfWidth - 218, halfHeight + 32, 16, 16, "You won, so type something");
			Text.drawText(halfWidth - 218, halfHeight + 16, 16, 16, "for the entire server to see!");
			
			Mouse.setGrabbed(false);
			if (textBox == null) {
				textBox = new TextBox(halfWidth - 224, halfHeight, 448, 16);
			} else {
				textBox.setBounds(halfWidth - 224, halfHeight);
			}
			
			textBox.testClick();
			textBox.render(new Color(0f, 0f, 0f, 0.5f));
			glBindTexture(GL_TEXTURE_2D, 0);
			
			String text = textBox.getText();
			if (!text.contentEquals("")) {
				if (Shadows.isServer) {
					TCPServer.sendMessage(new WonPacket(Shadows.name + ":::::" + text));
				} else if (Shadows.connected) {
					TCPClient.sendMessage(new WonPacket(Shadows.name + ":::::" + text));
				}
				showMessageBox = false;
				textBox.setText("");
				
				textBox.setSelected(false);
			}
		} else if (showMessage && !messageShown) {
			if (x < 90f) {
				float cosX = 1;
				if (x > 0) {
					cosX = (float) Math.cos(Math.toRadians((double)x));
				}
				
				if (showBackground) {
					glPushMatrix();
					glScalef(Display.getWidth(), Display.getHeight(), 1);
					GUIObject.textureBox.renderStart(false);
					glColor4f(0, 0, 0, cosX);
					GUIObject.textureBox.renderFinish(GL_TRIANGLES);
					glColor4f(1, 1, 1, 1);
					glPopMatrix();
				}
				
				String messageString = message;
				Text.drawText(halfWidth, halfHeight, 16, 16, messageString, new Color(cosX, cosX, cosX, 1));
			} else {
				showMessage = false;
				messageShown = true;
				x = startX;
			}
			
			//0.09375 multiplied by 16 (the delta for 60 fps) produces 1.5
			x += 0.09375f * Shadows.g.delta;
		}
	}

	public static boolean messageBoxShowed() {
		return messageShown;
	}
	
	public static boolean messageShowing() {
		return showMessage;
	}

	public static void setShowed(boolean showed) {
		messageShown = showed;
	}

	public static void setMessage(String message, boolean showBlack) {
		showBackground = showBlack;
		if (showBlack) {
			if (startX != -1350) {
				x = -1350;
				startX = -1350;
			}
		} else {
			x = -200;
			startX = -200;
		}
		setMessage(message);
	}
}