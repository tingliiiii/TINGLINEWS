package com.example.demo.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class PasswordUtil {

	// 生成隨機鹽值（Hex）
	public static String generateSalt() {
		byte[] salt = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		return KeyUtil.bytesToHex(salt);
	}

	// 使用鹽值和密碼生成哈希
	public static String hashPassword(String password, String salt) throws Exception {

		// 獲取 SHA-256 消息摘要物件來幫助我們生成密碼的哈希
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		// 加鹽
		byte[] saltBytes = KeyUtil.hexStringToByteArray(salt);
		messageDigest.update(saltBytes);

		// 將密碼轉換為 byte[] 然後生成哈希
		byte[] hash = messageDigest.digest(password.getBytes());
		
		// 加鹽後的哈希密碼
		return KeyUtil.bytesToHex(hash);
	}
}
