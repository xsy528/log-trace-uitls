package cn.gyyx.aoplog.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * .Net算法移植类
 * 用以计算给定日志的id值<br />
 * 参考：Gyyx.Module.LogV2.Common.Codec.LogHash
 * @author Administrator
 */
public class Hashs {
	
	private static final String key = "gyyx";
	private static final char[] chars = new char[] { 'a', 'b', 'c', 'd', 'e',
			'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r',
			's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H',
			'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
			'V', 'W', 'X', 'Y', 'Z' };
	
	public static String compute(String text) {
		StringBuilder builder = new StringBuilder();
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] md5byte = md5.digest((text + key).getBytes("UTF-8"));
			int num = 0x3FFFFFFF & byteArrayToInt(md5byte, 0);
			builder.delete(0, builder.length());
			for (int i = 0; i < 6; i++) {
				int index = 0x0000003D & num;
				builder.append(chars[index]);
				num = num >> 5;
			}
		} catch (NoSuchAlgorithmException e){
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return builder.toString();
	}

	static int byteArrayToInt(byte[] b, int offset) {
		int value = 0;
		for (int i = 0; i < 4; i++) {
			int shift = (4 - 1 - i) * 8;
			value += (b[i + offset] & 0x000000FF) << shift;
		}
		return value;
	}

}
