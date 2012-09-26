package de.uvwxy.packsock.game;

import de.uvwxy.packsock.Packet;
import de.uvwxy.packsock.PacketType;
import de.uvwxy.packsock.chat.ChatClient;
import de.uvwxy.packsock.chat.ChatMessage;
import de.uvwxy.packsock.chat.IChatMessageHook;

public class GameClient extends ChatClient {
	IGameMessageHook gameHook;

	long id = System.currentTimeMillis();

	public GameClient(int port, String address, IChatMessageHook msgHook, IGameMessageHook gameHook) {
		super(port, address, msgHook);
		this.gameHook = gameHook;
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
		case PacketType.GAME_MESSAGE:
			GameMessage m1 = new GameMessage(p.getPayloadAsBytes());
			if (gameHook != null)
				gameHook.onMessageReceived(m1);
			break;
		default:
		}
	}

	public void loginOnServer() {

	}
}