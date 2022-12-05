package com.shadows.networking;

public class FirePlayerPacket extends PlayerPacket {
	private static final long serialVersionUID = 3118478413165911692L;
	private float rotationX, rotationY, rotationZ;
	private String projName = "";
	
	public FirePlayerPacket(float x, float y, float z, String name, float rotationX, float rotationY, float rotationZ) {
		super(x, y, z, name);
		this.rotationX = rotationX;
		this.rotationY = rotationY;
		this.rotationZ = rotationZ;
		this.projName = name;
	}

	public String getProjName() {
		return projName;
	}

	public float getRotationX() {
		return rotationX;
	}

	public float getRotationY() {
		return rotationY;
	}

	public float getRotationZ() {
		return rotationZ;
	}
}