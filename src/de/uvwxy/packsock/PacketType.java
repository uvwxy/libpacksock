package de.uvwxy.packsock;

public class PacketType {
	
	
	public static final byte STRING = 0;
	public static final byte BINARY = 1;
	public static final byte CHAT_MESSAGE = 2;
	public static final byte GAME_DATA = 3;
	
	private byte value;

	PacketType(byte value) {
		this.value = value;
	}

	PacketType(int value) {
		this.value = (byte) value;
	}

	public byte getByteRepresentation() {
		return value;
	}

	public boolean equals(PacketType t) {
		if (t == null) {
			return false;
		}
		return t.getByteRepresentation() == value;
	}

	public void setType(byte b) {
		value = b;
	}
}
