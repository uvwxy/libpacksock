package de.uvwxy.packsock;

public class PackSockMonitor implements Runnable {
	private PackSock socket;
	private PacketHook hook;
	private boolean running = true;

	public PackSockMonitor(PackSock socket, PacketHook hook) {
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
				running = false;
				System.out.println("Error reading socket, stopping monitor (" + e.getLocalizedMessage() + ")");
			}

			if (temp != null) {
				if (hook != null) {
					hook.onMessageReceived(temp);
				}
			}
		}
	}
}
