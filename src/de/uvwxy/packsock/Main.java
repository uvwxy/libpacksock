package de.uvwxy.packsock;

import de.uvwxy.packsock.chat.ChatClient;
import de.uvwxy.packsock.chat.ChatMessage;
import de.uvwxy.packsock.chat.IChatMessageHook;
import de.uvwxy.packsock.chat.ChatServer;

/**
 * A class to test this library. Using the same ports, or different ports alternatingly does not break anything (so far
 * ;) ).
 * 
 * @author Paul Smith
 * 
 */
public class Main {

	public static IChatMessageHook cmh0 = new IChatMessageHook() {

		@Override
		public void onMessageReceived(ChatMessage msg, PackSock inSockf) {
			System.out.println("Client0: " + msg);
		}
	};

	public static IChatMessageHook cmh1 = new IChatMessageHook() {

		@Override
		public void onMessageReceived(ChatMessage msg, PackSock inSock) {
			System.out.println("Client1: " + msg);
		}
	};

	public static IChatMessageHook cmh2 = new IChatMessageHook() {

		@Override
		public void onMessageReceived(ChatMessage msg, PackSock inSock) {
			System.out.println("Client2: " + msg);
		}
	};

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		ChatServer server = new ChatServer(25567, 4, "Friedo");

		log("Starting server");
		
		server.start();

		log("Starting clients");
		
		ChatClient client0 = new ChatClient(25567, "localhost", cmh0);
		ChatClient client1 = new ChatClient(25567, "localhost", cmh1);
		ChatClient client2 = new ChatClient(25567, "localhost", cmh2);

		// needs some time otherwise new listener thread not ready
		client0.connect();
		Thread.sleep(1000);
		client1.connect();
		Thread.sleep(1000);
		client2.connect();
		
		ChatMessage m0 = new ChatMessage("client0", "fancy text");
		ChatMessage m1 = new ChatMessage("client1", "fancy text");
		ChatMessage m2 = new ChatMessage("client2", "fancy text");

//		client0.disconnect();
		
		log("Sending Messages");
		
		client0.sendMessage(m0);
		client1.sendMessage(m1);
		client2.sendMessage(m2);
		
		Thread.sleep(1000);
		
		server.stop();
		
		client0.disconnect();
		client1.disconnect();
		client2.disconnect();
	}

	private static void log(String s) {
		System.out.println(s);
	}

	@SuppressWarnings("unused")
	private static void oldTests() throws Exception {
		ChatMessage msg = new ChatMessage("Paul", "Hello World");

		byte[] msgAsBytes = msg.getObjectData();

		ChatMessage test = new ChatMessage(msgAsBytes);

		log("MSG: " + test);

		Packet receivedPacket = null;

		PackSock server0 = new PackSock(25566, null);
		PackSock client0 = new PackSock("127.0.0.1", 25566);

		server0.listen();

		client0.connect();
		client0.sendPacket(new Packet(PacketType.STRING, "Mooh"));

		PackSock server1 = new PackSock(25567, null);
		PackSock client1 = new PackSock("127.0.0.1", 25567);

		receivedPacket = server0.blockingReadSocketForPacket();
		log("String: \"" + receivedPacket.getPayloadAsString() + "\"");

		server1.listen();

		client1.connect();
		client1.sendPacket(new Packet(PacketType.STRING, "MuuH"));

		PackSock server2 = new PackSock(25566, null);
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

		Packet chatMessage = new Packet(PacketType.BINARY, msg.getObjectData());
		server0.sendPacket(chatMessage);
		receivedPacket = client0.blockingReadSocketForPacket();
		ChatMessage receivedMessage = new ChatMessage(receivedPacket.getPayloadAsBytes());
		log("Recvied message: " + receivedMessage);
	}

}
