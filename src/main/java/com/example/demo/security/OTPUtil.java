package com.example.demo.security;

import java.security.SecureRandom;


public class OTPUtil {

	// 生成一個六位數的 OTP
	public static String generateOTP() {
		SecureRandom secureRandom = new SecureRandom();
		int number = secureRandom.nextInt(1000000);
		return String.format("%06d", number);
	}

}
