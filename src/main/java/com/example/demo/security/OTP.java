package com.example.demo.security;

import java.security.SecureRandom;


public class OTP {

	// 生成一個六位數的 OTP
	public static String generateOTP() {
		SecureRandom secureRandom = new SecureRandom();
		int number = secureRandom.nextInt(1000000);
		return String.format("%06d", number);
	}

	public static void main(String[] args) {
		String otp = generateOTP();
		System.out.printf("您的 OTP: %s%n", otp);
	}

}
