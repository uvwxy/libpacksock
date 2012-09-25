package de.uvwxy.packsock;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class Packet {
	private static final String CHARSET = "UTF-8";
	private PacketType t;

	private byte[] payload;

	public Packet() {
		// nothing
	}

	public Packet(byte b, String msg) {
		PacketType x = PacketType.BINARY;
		x.setType(b);
		this.t = x;
		this.payload = string2bytes(msg);
	}

	public Packet(PacketType t, String msg) {
		this.t = t;
		this.payload = string2bytes(msg);
	}

	private byte[] string2bytes(String s) {
		return s.getBytes(Charset.forName(CHARSET));
	}

	private String bytes2string(byte[] b) {
		return new String(b, Charset.forName(CHARSET));
	}

	/**
	 * Set the given byte array as payload. Does not concatenate!
	 * 
	 * @param payload
	 */
	public void setPayloadAsBytes(byte[] b) {
		this.payload = b;
	}

	/**
	 * Set the given string as payload. Does not concatenate!
	 * 
	 * @param payload
	 */
	public void setPayloadAsString(String payload) {
		this.payload = string2bytes(payload);
	}

	/**
	 * Set the type of this packet.
	 * 
	 * @param t
	 */
	public void setT(PacketType t) {
		this.t = t;
	}

	/**
	 * Set the byte representation of the type of this packet.
	 * 
	 * @param b
	 */
	public void setT(byte b) {
		if (t == null) {
			PacketType x = PacketType.BINARY;
			x.setType(b);
			this.t = x;
			t = x;
		} else
			t.setType(b);
	}

	/**
	 * Return the byte representation of the type of this packet.
	 * 
	 * @return
	 */
	public byte getTypeByte() {
		return t.getByteRepresentation();
	}

	/**
	 * Concatenate the payload with this byte array.
	 * 
	 * @param b
	 */
	public void addBytesToPayload(byte[] b) {
		// Only add bytes when getPayloadLengthBytes has not been requested (otherwise inconsistency) ?

		if (payload == null) {
			payload = b;
			return;
		}

		byte[] new_payload = new byte[payload.length + b.length];
		for (int i = 0; i < payload.length; i++) {
			new_payload[i] = payload[i];
		}

		for (int i = 0; i < b.length; i++) {
			new_payload[i + payload.length] = b[i];

		}

		payload = new_payload;
	}

	/**
	 * Return the payload interpreted as a string.
	 * 
	 * @return
	 */
	public String getPayloadAsString() {
		return bytes2string(payload);
	}

	/**
	 * Return the length of the payload.
	 * 
	 * @return an integer split into a byte array. (msb left)
	 */
	public byte[] getPayloadLengthBytes() {
		if (payload == null || t == null) {
			return null;
		}

		return ByteBuffer.allocate(4).putInt(payload.length).array();
	}

	/**
	 * Read the type of this packet
	 * 
	 * @return the type
	 */
	public PacketType getType() {
		return this.t;
	}

	/**
	 * Read the payload of this packet as a byte array.
	 * 
	 * @return the byte array of the payload
	 */
	public byte[] getPayloadAsBytes() {
		return payload;
	}
}
