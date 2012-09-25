package de.uvwxy.packsock.chat;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

import de.uvwxy.packsock.PackSock;
import de.uvwxy.packsock.PackSockMonitor;
import de.uvwxy.packsock.Packet;
import de.uvwxy.packsock.PacketHook;
import de.uvwxy.packsock.ServerConnectedHook;

public class ChatServer implements ServerConnectedHook, PacketHook {
	int port;
	int maxConnectionCount;
	String ServerName;
	PackSock currentListener = null;
	LinkedList<PackSock> sockets = new LinkedList<PackSock>();
	LinkedList<PackSockMonitor> monitors = new LinkedList<PackSockMonitor>();

	boolean no_further_listener = false;

	private class ListenThread implements Runnable {

		@Override
		public void run() {
			try {
				currentListener.listen();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public ChatServer(int port, int maxConnectionCount, String serverName) {
		super();
		this.port = port;
		this.maxConnectionCount = maxConnectionCount;
		ServerName = serverName;
	}

	public void start() throws IOException {
		System.out.println("Starting listener on port " + port);
		currentListener = new PackSock(port, this);
		new Thread(new ListenThread()).start();
	}

	@Override
	public void onServerAcceptedConnection() {
		System.out.println("Listening");
		PackSockMonitor m = new PackSockMonitor(currentListener, this);
		Thread t = new Thread(m);
		t.start();
		monitors.add(m);
		sockets.add(currentListener);
		try {
			if (!no_further_listener)
				start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onMessageReceived(Packet p) {
		LinkedList<PackSock> remSockets = null;

		for (PackSock s : sockets) {
			try {
				s.sendPacket(p);
			} catch (SocketException se) {
				if (remSockets == null) {
					remSockets = new LinkedList<PackSock>();
				}
				remSockets.add(s);
				System.out.println("Socket was broken, removing..");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (remSockets != null) {
			for (PackSock s : remSockets){
				sockets.remove(s);
			}
		}
	}

	public void stop() throws IOException {
		for (PackSockMonitor m : monitors) {
			if (m != null)
				m.cancelPolling();
		}
		currentListener.stopListen();
		no_further_listener = true;
	}
}