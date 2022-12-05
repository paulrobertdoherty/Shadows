package com.shadows.networking;

import java.io.*;
import java.net.*;

public class UDPClient {
	private static DatagramSocket socket = null;
	private static InetAddress address = null;
	
	public static void sendPacket(Object packet, String ip) throws IOException {
		if (TCPServer.port == 0) {
			TCPServer.getPort();
		}
		if (socket == null) {
			socket = new DatagramSocket(TCPServer.port + 1);
		}
		if (address == null) {
			address = InetAddress.getByName(ip);
		}
		byte[] data = new byte[256];
		data = serialize(packet);
		DatagramPacket packetHolder = new DatagramPacket(data, data.length, address, 4445);
		socket.send(packetHolder);
	}
	
	public static Object lookForReceived() throws IOException, ClassNotFoundException {
		if (TCPServer.port == 0) {
			TCPServer.getPort();
		}
		if (socket == null) {
			socket = new DatagramSocket(TCPServer.port + 1);
		}
		byte[] data = new byte[256];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		socket.receive(packet);
		return deserialize(packet.getData());
	}
	
	public static byte[] serialize(Object object) throws IOException {
		ByteArrayOutputStream o = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(o);
		oos.writeObject(object);
		return o.toByteArray();
	}
	
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
		ByteArrayInputStream o = new ByteArrayInputStream(data);
		ObjectInputStream ois = new ObjectInputStream(o);
		Object object = ois.readObject();
		ois.close();
		o.close();
		return object;
	}
	
	public static void close() throws IOException {
		socket.close();
	}
}