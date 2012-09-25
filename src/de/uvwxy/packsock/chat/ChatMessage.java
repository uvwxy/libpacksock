package de.uvwxy.packsock.chat;

import java.nio.ByteBuffer;

import de.uvwxy.packsock.BytesConverter;
import de.uvwxy.packsock.PackSockMessage;

public class ChatMessage implements PackSockMessage {
	private long timestamp;
	private String sender;
	private String msg;

	public ChatMessage() {
		timestamp = System.currentTimeMillis();
	}

	public ChatMessage(String sender, String msg) {
		this();
		this.sender = sender;
		this.msg = msg;
	}

	public ChatMessage(byte[] data) {
		setByteArrayData(data);
	}

	@Override
	public byte[] getByteArrayData() {
		byte[] bTimestamp = BytesConverter.long2bytes(timestamp);

		byte[] bSender = BytesConverter.string2bytes(sender);
		byte[] bSenderLen = BytesConverter.int2bytes(bSender.length);

		byte[] bMsg = BytesConverter.string2bytes(msg);
		byte[] bMsgLen = BytesConverter.int2bytes(bMsg.length);

		byte[] bAll = new byte[bTimestamp.length + bSenderLen.length + bSender.length + bMsgLen.length + bMsg.length];

		System.arraycopy(bTimestamp, 0, bAll, 0, bTimestamp.length);

		System.arraycopy(bSenderLen, 0, bAll, bTimestamp.length, bSenderLen.length);
		System.arraycopy(bSender, 0, bAll, bSenderLen.length + bTimestamp.length, bSender.length);

		System.arraycopy(bMsgLen, 0, bAll, bSender.length + bSenderLen.length + bTimestamp.length, bMsgLen.length);
		System.arraycopy(bMsg, 0, bAll, bMsgLen.length + bSender.length + bSenderLen.length + bTimestamp.length,
				bMsg.length);

		return bAll;
	}

	@Override
	public void setByteArrayData(byte[] data) {
		ByteBuffer bb = ByteBuffer.wrap(data);

		long timestamp = bb.getLong();
		int senderLen = bb.getInt();
		bb.position(bb.position() + senderLen);
		int msgLen = bb.getInt();

		String sender = new String(data, 12, senderLen);
		String msg = new String(data, 12 + senderLen + 4, msgLen);

		this.timestamp = timestamp;
		this.sender = sender;
		this.msg = msg;
	}

	public String toString() {
		return "" + timestamp + ": " + sender + ": " + msg;
	}

}
