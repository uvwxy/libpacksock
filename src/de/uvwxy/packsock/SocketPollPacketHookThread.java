package de.uvwxy.packsock;

public class SocketPollPacketHookThread implements Runnable {
	private PackSock socket;
	private IPacketHook hook;
	private boolean running = true;

	public SocketPollPacketHookThread(PackSock socket, IPacketHook hook) {
		this.socket = socket;
		this.hook = hook;
	}

	public void cancelPolling() {
		running = false;
	}

	@Override
	public void run() {
		Packet temp = null;
		while (running) {
			try {
				temp = socket.tryReadSocketForPacket();
			} catch (Exception e) {
				// ioexception if socket closed, or other io error -> socket is broken thus stop polling
				running = false;
				System.out.println("Error reading socket, stopping monitor (" + e.getLocalizedMessage() + ")");
			}

			if (temp != null && hook != null) {
				hook.onMessageReceived(temp);
			}
		}
	}
}
