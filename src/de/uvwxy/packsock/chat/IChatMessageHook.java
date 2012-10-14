package de.uvwxy.packsock.chat;

import de.uvwxy.packsock.PackSock;

public interface IChatMessageHook {
	public void onMessageReceived(ChatMessage msg, PackSock inSock);
}
