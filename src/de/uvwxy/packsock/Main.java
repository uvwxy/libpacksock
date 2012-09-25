package de.uvwxy.packsock;

import java.io.IOException;
import java.net.InetAddress;

/**
 * A class to test this library. Using the same ports, or different ports alternatingly does not break anything (so far
 * ;) ).
 * 
 * @author Paul Smith
 * 
 */
public class Main {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Packet receivedPacket = null;

		PackSock server0 = new PackSock(25566);
		PackSock client0 = new PackSock("127.0.0.1", 25566);

		server0.listen();

		client0.connect();
		client0.sendPacket(new Packet(PacketType.STRING, "Mooh"));

		PackSock server1 = new PackSock(25567);
		PackSock client1 = new PackSock("127.0.0.1", 25567);

		receivedPacket = server0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		server1.listen();

		client1.connect();
		client1.sendPacket(new Packet(PacketType.STRING, "MuuH"));

		PackSock server2 = new PackSock(25566);
		PackSock client2 = new PackSock("127.0.0.1", 25566);

		server2.listen();

		client2.connect();
		client2.sendPacket(new Packet(PacketType.STRING, "client 2 ok"));

		receivedPacket = server1.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		server0.sendPacket(new Packet(PacketType.STRING, "Maah"));
		server0.sendPacket(new Packet(PacketType.STRING, "Määh"));
		server0.sendPacket(new Packet(PacketType.STRING, "Mööh"));

		receivedPacket = client0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		receivedPacket = client0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		receivedPacket = client0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		receivedPacket = server2.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		server0.sendPacket(new Packet(PacketType.STRING, "server 0 ok"));
		server1.sendPacket(new Packet(PacketType.STRING, "server 1 ok"));
		server2.sendPacket(new Packet(PacketType.STRING, "server 2 ok"));

		client0.sendPacket(new Packet(PacketType.STRING, "client 0 ok"));
		client1.sendPacket(new Packet(PacketType.STRING, "client 1 ok"));
		client2.sendPacket(new Packet(PacketType.STRING, "client 2 ok"));

		receivedPacket = server0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
		receivedPacket = server1.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
		receivedPacket = server2.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
		receivedPacket = client0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
		receivedPacket = client1.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
		receivedPacket = client2.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");
	}

	private static void log(String s) {
		System.out.println(s);
	}

}
