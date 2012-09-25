package de.uvwxy.packsock.chat;

import java.io.IOException;

import de.uvwxy.packsock.PackSock;
import de.uvwxy.packsock.SocketPollPacketHookThread;
import de.uvwxy.packsock.Packet;
import de.uvwxy.packsock.IPacketHook;
import de.uvwxy.packsock.PacketType;

public class ChatClient implements IPacketHook {
	protected IChatMessageHook msgHook;

	PackSock socket = null;
	SocketPollPacketHookThread monitor = null;

	public ChatClient(int port, String address, IChatMessageHook msgHook) {
		socket = new PackSock(address, port);
		this.msgHook = msgHook;
	}

	public void connect() throws Exception {
		System.out.println("Starting connection");
		socket.connect();
		monitor = new SocketPollPacketHookThread(socket, this);
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
