package de.uvwxy.packsock.game;

import de.uvwxy.packsock.PackSock;

public interface IGameMessageHook {
	public void onMessageReceived(GameMessage msg, PackSock s);
}
