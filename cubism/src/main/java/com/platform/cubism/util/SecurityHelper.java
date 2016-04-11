package com.platform.cubism.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class SecurityHelper {
	// 定义加密算法，有DES、DESede(即3DES)、Blowfish
	private static final String Algorithm = "DESede";
	private static SecretKey DEFAULT_CRYPT_KEY;
	static {
		Security.addProvider(new com.sun.crypto.provider.SunJCE());
		try {
			DEFAULT_CRYPT_KEY = KeyGenerator.getInstance(Algorithm).generateKey();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public static String bytes2HexString(byte[] b) {
		if (b == null) {
			return null;
		}
		String hex;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < b.length; i++) {
			hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			sb.append(hex.toUpperCase());
		}
		return sb.toString();
	}

	public static byte[] hexStringToBytes(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	private static byte charToByte(char c) {
		int index = "0123456789ABCDEF".indexOf(c);
		if (index == -1) {
			index = "0123456789abcdef".indexOf(c);
		}
		return (byte) index;
	}

	public static String DesEncrypt(String src) {
		return DesEncrypt(src, DEFAULT_CRYPT_KEY);
	}

	public static String DesDecrypt(String src) {
		return DesDecrypt(src, DEFAULT_CRYPT_KEY);
	}

	public static String DesEncrypt(String src, String key) {
		try {
			return DesEncrypt(src, get3DesKey(key));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String DesDecrypt(String src, String key) {
		try {
			return DesDecrypt(src, get3DesKey(key));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String DesEncrypt(String src, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.ENCRYPT_MODE, key);
			byte[] b = src.getBytes();
			return bytes2HexString(cipher.doFinal(b));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String DesDecrypt(String src, SecretKey key) {
		try {
			Cipher cipher = Cipher.getInstance(Algorithm);
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] b = hexStringToBytes(src);
			return new String(cipher.doFinal(b));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * 根据字符串生成密钥字节数组
	 * 
	 * @param keyStr 密钥字符串
	 * 
	 * @return
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private static SecretKey get3DesKey(String keyStr) throws UnsupportedEncodingException {
		return get3DesKey(keyStr, Algorithm);
	}

	private static SecretKey get3DesKey(String keyStr, String algorithm) throws UnsupportedEncodingException {
		if (keyStr == null || keyStr.length() <= 0) {
			throw new RuntimeException("加解密的密钥不能为空！");
		}
		byte[] key = new byte[24]; // 声明一个24位的字节数组，默认里面都是0
		byte[] temp = keyStr.getBytes("UTF-8"); // 将字符串转成字节数组

		/*
		 * 执行数组拷贝 System.arraycopy(源数组，从源数组哪里开始拷贝，目标数组，拷贝多少位)
		 */
		if (key.length > temp.length) {
			// 如果temp不够24位，则拷贝temp数组整个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, temp.length);
		} else {
			// 如果temp大于24位，则拷贝temp数组24个长度的内容到key数组中
			System.arraycopy(temp, 0, key, 0, key.length);
		}
		return new SecretKeySpec(key, algorithm);
	}
}
