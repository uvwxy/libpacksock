package de.uvwxy.packsock;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * A class to simplify the transmission of binary data or strings with a tiny packet header. Its payload size is limited
 * by BUFFER_SIZE.
 * 
 * @author Paul Smith code@uvwxy.de
 * 
 */
public class PackSock {
	// 64 Kb packet size should be enough?
	private static final int BUFFER_SIZE = 1024 * 64;
	private static final int BYTES_TO_READ_FOR_SIZE = 4;
	private static final int BYTES_TO_READ_FOR_TYPE = 1;

	private byte[] buffer = new byte[BUFFER_SIZE];
	private int buffer_pointer = 0;
	private int bytes_to_read_for_payload = -1;
	private int bytes_read_size = 0;
	private int bytes_read_type = 0;
	private int bytes_read_payload = 0;

	private String serverIpAddress;
	private int connectionPort;
	private InetAddress serverAddress;

	private SocketReadState state = SocketReadState.READ_NOTHING;
	private static ServerSocket serverListener;
	private boolean isServer = false;
	private Socket clientSocket;
	private Socket serverSocket;
	private BufferedInputStream sock_in;
	private BufferedOutputStream sock_out;
	private Packet bufferedPacket = null;
	private IServerConnectedHook hook = null;
	
	private class ListenThread implements Runnable {
		@Override
		public void run() {
			try {
				try{
				serverSocket = serverListener.accept();
				} catch (SocketException se){
					// TODO: has been killed by serverListener.close() or other reason
					return;
				}
				sock_in = new BufferedInputStream(serverSocket.getInputStream());
				sock_out = new BufferedOutputStream(serverSocket.getOutputStream());
				if (hook!=null)
					hook.onServerAcceptedConnection();
			} catch (IOException e) {
				System.out.println("Socket input/output stream broken..");
				e.printStackTrace();
			} 
		}

	}

	/**
	 * Initialize this <code>PackSock</code> as a client socket.
	 * 
	 * @param port
	 * @throws IOException
	 */
	public PackSock(int port, IServerConnectedHook hook) throws IOException {
		isServer = true;
		this.hook = hook;
		if (serverListener == null) {
			serverListener = new ServerSocket(port);
			this.connectionPort = port;
		}
		if (port != this.connectionPort) {
			this.connectionPort = port;
			// This does _not_ close previously connected sockets:
			serverListener.close();
			serverListener = new ServerSocket(port);
		}

	}

	/**
	 * Initialize this <code>PackSock</code> as a server socket.
	 * 
	 * @param serverIpAddress
	 * @param port
	 */
	public PackSock(String serverIpAddress, int port) {
		this.serverIpAddress = serverIpAddress;
		this.connectionPort = port;
	}

	/**
	 * Sets this socket into listen mode with a background Thread accepting the connection.
	 * 
	 * @throws IOException
	 */
	public void listen() throws IOException {
		if (!isServer)
			return;
		Thread listener = new Thread(new ListenThread());
		listener.start();
	}

	/**
	 * Tries to connect this socket to the given host as client.
	 * 
	 * @throws Exception
	 */
	public void connect() throws Exception {
		if (isServer)
			return;

		serverAddress = InetAddress.getByName(serverIpAddress);

		if (clientSocket != null)
			if (clientSocket.isConnected())
				clientSocket.close();

		clientSocket = new Socket(serverAddress, connectionPort);
		clientSocket.setSoTimeout(0);
		clientSocket.setKeepAlive(true);
		clientSocket.setTcpNoDelay(true);

		sock_in = new BufferedInputStream(clientSocket.getInputStream());
		sock_out = new BufferedOutputStream(clientSocket.getOutputStream());

		return;
	}

	/**
	 * This method takes the given <code>Packet</code> and sends it through the existing connection.
	 * 
	 * @param p
	 *            the <code>Packet</code> to send. <code>null</code> is not sent.
	 * @throws IOException
	 */
	public void sendPacket(Packet p) throws IOException, SocketException {
		if (p == null || sock_out == null) {
			// should throw exception here!
			return;
		}

		sock_out.write(p.getPayloadLengthBytes());
		sock_out.write(p.getTypeByte());
		sock_out.write(p.getPayloadAsBytes());
		// make sure stream is written out to the target stream
		sock_out.flush();

	}

	/**
	 * This function will block until a <code>Packet</code> is received. This function will not return <code>null</code>
	 * .
	 * 
	 * @return
	 * @throws IOException
	 */
	public Packet blockingReadSocketForPacket() throws IOException {
		Packet buf = null;
		while (buf == null)
			buf = tryReadSocketForPacket();

		return buf;
	}

	/**
	 * This function will try to read packet data from the socket. If not enough data has been received so far
	 * <code>null</code> is returned. Once an entire packet is read from the buffer the <code>Packet</code> is returned.
	 * returned.
	 * 
	 * @throws IOException
	 */
	public Packet tryReadSocketForPacket() throws IOException, SocketException {
		if (sock_in == null) {
			return null;
		}

		switch (state) {
		case READ_PACKET:
			// reset everything
			bytes_read_size = 0;
			bytes_read_type = 0;
			bytes_read_payload = 0;
			buffer_pointer = 0;
		case READ_NOTHING:
			bufferedPacket = new Packet();
		case READING_SIZE:
			
			// read maximal 4 bytes of size field
			bytes_read_size += sock_in.read(buffer, buffer_pointer, BYTES_TO_READ_FOR_SIZE - bytes_read_size);
			buffer_pointer += bytes_read_size;

			if (bytes_read_size == BYTES_TO_READ_FOR_SIZE) {
				// now we have everything!
				byte[] src = buffer;
				int srcOffset = buffer_pointer - 4;
				int byteCount = 4;

				// reassemble integer
				ByteBuffer b = ByteBuffer.allocate(4).put(src, srcOffset, byteCount);
				b.position(0);
				int size = b.getInt();
				// set bytes_to_read_payload
				bytes_to_read_for_payload = size;
				state = SocketReadState.READING_TYPE;
			}

			break;

		case READING_TYPE:
			bytes_read_type += sock_in.read(buffer, buffer_pointer, BYTES_TO_READ_FOR_TYPE - bytes_read_type);
			buffer_pointer += bytes_read_type;

			if (bytes_read_type == BYTES_TO_READ_FOR_TYPE) {
				// now we have everything!
				byte type = buffer[buffer_pointer - 1];
				bufferedPacket.setT(type);
				state = SocketReadState.READING_PAYLOAD;
			}

			break;

		case READING_PAYLOAD:
			int bytesCurrentlyRead = sock_in.read(buffer, buffer_pointer, bytes_to_read_for_payload
					- bytes_read_payload);
			bytes_read_payload += bytesCurrentlyRead;
			buffer_pointer += bytesCurrentlyRead;

			if (bytes_read_payload == bytes_to_read_for_payload) {
				// now we have everything!
				byte[] src = buffer;
				int srcOffset = buffer_pointer - bytes_to_read_for_payload;
				int byteCount = bytes_to_read_for_payload;

				byte[] buf = new byte[byteCount];

				for (int i = 0; i < byteCount; i++) {
					buf[i] = src[i + srcOffset];
				}

				bufferedPacket.setPayloadAsBytes(buf);

				state = SocketReadState.READ_PACKET;
				return bufferedPacket;
			}

			break;
		}

		return null;
	}

	private enum SocketReadState {
		READ_NOTHING, READING_SIZE, READING_TYPE, READING_PAYLOAD, READ_PACKET
	}

	public String getHostAdress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	public String getHostName() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostName();
	}

	public void stopListen() throws IOException {
		serverListener.close();
	}
	
	public void disconnect() throws IOException{
		clientSocket.close();
	}

}
