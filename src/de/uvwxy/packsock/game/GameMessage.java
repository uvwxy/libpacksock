package de.uvwxy.packsock.game;

import java.nio.ByteBuffer;

import de.uvwxy.packsock.BytesConverter;
import de.uvwxy.packsock.IPackSockMessage;

public class GameMessage implements IPackSockMessage {
	private long timestamp;

	private byte id;

	private byte[] gameObjData;

	/**
	 * Use this constructor to create the game message from a pack sock message
	 * 
	 * @param objData
	 *            can not be null?
	 */
	public GameMessage(byte[] objData) {
		setObjData(objData);
	}

	/**
	 * Use this constructor to create a new message with game data
	 * 
	 * @param id
	 * @param game_data
	 *            can not be null?
	 */
	public GameMessage(byte id, byte[] game_data) {
		timestamp = System.currentTimeMillis();
		this.id = id;
		this.gameObjData = game_data;
	}

	public byte[] getGameObjData() {
		return gameObjData;
	}

	public byte getId() {
		return id;
	}

	/**
	 * Returns the "serialized" representation of this objects data
	 */
	@Override
	public byte[] getObjectData() {
		byte[] bTimestamp = BytesConverter.long2bytes(timestamp);
		byte[] bGameLen = BytesConverter.int2bytes(gameObjData != null ? gameObjData.length : 0);

		byte[] bAll = new byte[bTimestamp.length + bGameLen.length + gameObjData.length + 1];

		System.arraycopy(bTimestamp, 0, bAll, 0, bTimestamp.length);

		bAll[bTimestamp.length] = id;

		System.arraycopy(bGameLen, 0, bAll, bTimestamp.length + 1, bGameLen.length);
		System.arraycopy(gameObjData, 0, bAll, bGameLen.length + bTimestamp.length + 1, gameObjData.length);
		return bAll;
	}

	@Override
	public void setObjData(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);

		long timestamp = bb.getLong();
		this.id = bb.get();
		int gameLen = bb.getInt();
		gameObjData = new byte[gameLen];

		System.arraycopy(data, bb.position(), gameObjData, 0, gameLen);

		this.timestamp = timestamp;
	}

	public String toString() {
		return "" + timestamp + "(" + id + "): " + (gameObjData.length) + " bytes of game data";
	}

}
