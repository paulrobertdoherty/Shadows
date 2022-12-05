package com.shadows.networking;

import java.io.*;
import java.net.*;
import java.util.*;

public class UDPServer {
	private static DatagramSocket socket = null;
	private static List<InetAddress> addresses = new ArrayList<InetAddress>();
	
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
		if (addresses.contains(packet.getAddress())) {
			addresses.add(packet.getAddress());
		}
		return UDPClient.deserialize(packet.getData());
	}
	
	public static void sendPacket(Object packet) throws IOException {
		sendPacket(UDPClient.serialize(packet));
	}
	
	private static void sendPacket(byte[] packetData) throws IOException {
		if (TCPServer.port == 0) {
			TCPServer.getPort();
		}
		if (socket == null) {
			socket = new DatagramSocket(TCPServer.port + 1);
		}
		for (InetAddress a : addresses) {
			DatagramPacket packet = new DatagramPacket(packetData, packetData.length, a, 4445);
			socket.send(packet);
		}
	}
}