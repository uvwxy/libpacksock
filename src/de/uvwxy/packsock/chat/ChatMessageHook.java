package de.uvwxy.packsock.chat;

public interface ChatMessageHook {
	public void onMessageReceived(ChatMessage msg);
}
