package de.uvwxy.packsock;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BytesConverter {
	private static final String CHARSET = "UTF-8";

	public static byte[] string2bytes(String s) {
		return s.getBytes(Charset.forName(CHARSET));
	}

	public static String bytes2string(byte[] b) {
		return new String(b, Charset.forName(CHARSET));
	}
	
	
	public static byte[] long2bytes(long l){
		return ByteBuffer.allocate(8).putLong(l).array();
	}
	
	public static long bytes2long(byte[] b){
		return ByteBuffer.wrap(b).getLong();
	}
	
	public static byte[] int2bytes(int i){
		return ByteBuffer.allocate(4).putInt(i).array();
	}
	
	public static int bytes2int(byte[] b){
		return ByteBuffer.wrap(b).getInt();
	}
}
