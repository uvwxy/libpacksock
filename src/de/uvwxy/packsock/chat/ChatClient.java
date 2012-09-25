package de.uvwxy.packsock.chat;

import java.io.IOException;

import de.uvwxy.packsock.PackSock;
import de.uvwxy.packsock.PackSockMonitor;
import de.uvwxy.packsock.Packet;
import de.uvwxy.packsock.PacketHook;
import de.uvwxy.packsock.PacketType;

public class ChatClient implements PacketHook {
	ChatMessageHook msgHook;

	PackSock socket = null;
	PackSockMonitor monitor = null;

	public ChatClient(int port, String address, ChatMessageHook msgHook) {
		socket = new PackSock(address, port);
		this.msgHook = msgHook;
	}

	public void connect() throws Exception {
		System.out.println("Starting connection");
		socket.connect();
		monitor = new PackSockMonitor(socket, this);
		new Thread(monitor).start();
	}

	@Override
	public void onMessageReceived(Packet p) {
		if (p == null) {
			return;
		}
		
		switch (p.getTypeByte()) {
		case PacketType.CHAT_MESSAGE:
			ChatMessage m = new ChatMessage(p.getPayloadAsBytes());
			if (msgHook != null)
				msgHook.onMessageReceived(m);
			break;
		default:
		}
	}

	public void sendMessage(ChatMessage m) throws IOException {
		Packet p = new Packet(PacketType.CHAT_MESSAGE, m.getByteArrayData());
		socket.sendPacket(p);
	}

	public void disconnect() throws IOException {
		monitor.cancelPolling();
		socket.disconnect();
	}
}