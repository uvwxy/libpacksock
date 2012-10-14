package de.uvwxy.packsock.game;

import java.nio.ByteBuffer;

import de.uvwxy.packsock.BytesConverter;
import de.uvwxy.packsock.IPackSockMessage;

public class GameMessage implements IPackSockMessage {
	private long timestamp;

	private byte id;

	private byte[] game_data;

	/**
	 * Use this constructor to create the game message from a pack sock message
	 * @param byte_array_data
	 */
	public GameMessage(byte[] byte_array_data) {
		setByteArrayData(byte_array_data);
	}

	/**
	 * Use this constructor to create a new message with game data
	 * @param id
	 * @param game_data
	 */
	public GameMessage(byte id, byte[] game_data) {
		timestamp = System.currentTimeMillis();
		this.game_data = game_data;
	}

	public byte getId() {
		return id;
	}

	@Override
	public byte[] getByteArrayData() {
		byte[] bTimestamp = BytesConverter.long2bytes(timestamp);
		byte[] bGameLen = BytesConverter.int2bytes(game_data.length);

		byte[] bAll = new byte[bTimestamp.length + bGameLen.length + game_data.length + 1];

		System.arraycopy(bTimestamp, 0, bAll, 0, bTimestamp.length);

		bAll[bTimestamp.length] = id;

		System.arraycopy(bGameLen, 0, bAll, bTimestamp.length + 1, bGameLen.length);
		System.arraycopy(game_data, 0, bAll, bGameLen.length + bTimestamp.length + 1, game_data.length);
		return bAll;
	}

	@Override
	public void setByteArrayData(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);

		long timestamp = bb.getLong();
		this.id = bb.get();
		int gameLen = bb.getInt();
		game_data = new byte[gameLen];

		System.arraycopy(data, bb.position(), game_data, 0, gameLen);

		this.timestamp = timestamp;
	}

	public String toString() {
		return "" + timestamp + "(" + id + "): " + (game_data.length) + " bytes of game data";
	}

}
