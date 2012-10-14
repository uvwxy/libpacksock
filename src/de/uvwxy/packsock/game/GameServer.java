package de.uvwxy.packsock.game;

import de.uvwxy.packsock.PackSock;
import de.uvwxy.packsock.Packet;
import de.uvwxy.packsock.PacketType;
import de.uvwxy.packsock.chat.ChatServer;

public class GameServer extends ChatServer {
	IGameMessageHook gameHook;

	public GameServer(int port, int maxConnectionCount, String serverName, IGameMessageHook gameHook) {
		super(port, maxConnectionCount, serverName);
		this.gameHook = gameHook;
	}

	@Override
	public void onMessageReceived(Packet p, PackSock inSocket) {
		// Distribue Chat Messages as before
		if (p.getType().getByteRepresentation() == PacketType.CHAT_MESSAGE) {
			super.onMessageReceived(p, inSocket);
		} else if (p.getType().getByteRepresentation() == PacketType.CHAT_MESSAGE) {
			// additionally handle game messages
			GameMessage m1 = new GameMessage(p.getPayloadAsBytes());
			if (gameHook != null)
				gameHook.onMessageReceived(m1, inSocket);
		}

	}
}
