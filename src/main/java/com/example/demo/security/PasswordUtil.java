package com.example.demo.security;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordUtil {

	// 生成隨機鹽值（Hex）
	public static String generateSalt() {
		byte[] salt = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(salt);
		return bytesToHex(salt);
	}

	// 使用鹽值和密碼生成哈希
	public static String hashPassword(String password, String salt) throws Exception {

		// 獲取 SHA-256 消息摘要物件來幫助我們生成密碼的哈希
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

		// 加鹽
		byte[] saltBytes = hexStringToByteArray(salt);
		messageDigest.update(saltBytes);

		// 將密碼轉換為 byte[] 然後生成哈希
		byte[] hash = messageDigest.digest(password.getBytes());
		
		// 加鹽後的哈希密碼
		return bytesToHex(hash);
	}
	
    /**
     * 將byte陣列轉換為十六進制格式的字串。
     * 這通常用於方便地顯示二進制數據，如數字簽名、摘要或加密的數據。
     * 
     * @param bytes 要轉換的byte陣列
     * @return 十六進制格式的字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
    
    /**
     * 要從十六進制格式的雜湊字串轉回 byte[]
     * 
     * @return 返回 byte[]。
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
