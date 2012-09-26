package de.uvwxy.packsock.game;

import java.nio.ByteBuffer;

import de.uvwxy.packsock.BytesConverter;
import de.uvwxy.packsock.IPackSockMessage;

public class GameMessage implements IPackSockMessage {
	private long timestamp;

	private byte[] game_data;

	public GameMessage() {
		timestamp = System.currentTimeMillis();
	}

	public GameMessage(byte[] game_data) {
		this();
		this.game_data = game_data;
	}

	@Override
	public byte[] getByteArrayData() {
		byte[] bTimestamp = BytesConverter.long2bytes(timestamp);
		byte[] bGameLen = BytesConverter.int2bytes(game_data.length);

		byte[] bAll = new byte[bTimestamp.length + bGameLen.length + game_data.length];

		System.arraycopy(bTimestamp, 0, bAll, 0, bTimestamp.length);

		System.arraycopy(bGameLen, 0, bAll, bTimestamp.length, bGameLen.length);
		System.arraycopy(game_data, 0, bAll, bGameLen.length + bTimestamp.length, game_data.length);
		return bAll;
	}

	@Override
	public void setByteArrayData(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);

		long timestamp = bb.getLong();
		int gameLen = bb.getInt();
		game_data = new byte[gameLen];

		System.arraycopy(data, bb.position(), game_data, 0, gameLen);

		this.timestamp = timestamp;
	}

	public String toString() {
		return "" + timestamp + ": " + (game_data.length) + " bytes of game data";
	}

}
