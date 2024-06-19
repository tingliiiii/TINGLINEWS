package com.example.demo.security;

import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class OTPUtil {

	// 生成一個六位數的 OTP
	public static String generateOTP() {
		SecureRandom secureRandom = new SecureRandom();
		int otp = secureRandom.nextInt(1000000);
		return String.format("%06d", otp);
	}

	/**
     * 生成基於時間的一次性密碼 (TOTP)。
     *
     * @param secret       Base64 編碼的秘密金鑰。
     * @param timeInterval 當前的時間間隔，用於計算 TOTP。
     * @param crypto       指定的加密算法，例如 "HMAC-SHA256"。
     * @return             返回計算出的 6 位 TOTP。
     * @throws NoSuchAlgorithmException   若指定的加密算法不可用或不存在，則拋出此異常。
     * @throws InvalidKeyException       若初始化 Mac 物件時使用的密鑰是無效的，則拋出此異常。
     */
	public static String generateTOTP(String secret, long timeInterval, String crypto) throws Exception {

		// 將 Base64 編碼的秘密金鑰解碼
		byte[] decodedKey = Base64.getDecoder().decode(secret);

		// 創建一個加密演算法實例，例如：HMAC-SHA256
		Mac mac = Mac.getInstance(crypto);

		// 用解碼後的鑰匙和原始(RAW)加密演算法初始化 Mac
		SecretKeySpec spec = new SecretKeySpec(decodedKey, "HmacSHA256");
		mac.init(spec);

		// 根據當前時間和給定的時間間隔計算 TOTP
		byte[] hmac = mac.doFinal(longToBytes(timeInterval));
		int offset = hmac[hmac.length - 1] & 0xF;
		long otp = (hmac[offset] & 0x7F) << 24 | (hmac[offset + 1] & 0xFF) << 16 | (hmac[offset + 2] & 0xFF) << 8
				| (hmac[offset + 3] & 0xFF);

		// 將其縮小為 6 位數字
		otp = otp % 1000000;

		return String.format("%06d", otp);
	}

	/**
     * 將給定的 long 值轉換成 byte 陣列。
     * 
     * 此方法會將一個 64 位元的 long 值轉換成一個 8 位元組的 byte 陣列，其中每個 byte 代表 long 的一個字節。
     * 轉換是從最低有效位元組開始的，即 result[7] 是 l 的最低有效位元組，result[0] 是最高有效位元組。
     * 
     * @param l 需要轉換的 long 值。
     * @return 表示給定 long 值的 byte 陣列。
     */
	private static byte[] longToBytes(long l) {
		byte[] result = new byte[8];
		for (int i = 7; i >= 0; i--) {
			result[i] = (byte) (l & 0xFF);
			l >>= 8;
		}
		return result;
	}

}
