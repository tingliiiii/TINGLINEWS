package com.example.demo.security;

import java.security.SecureRandom;
import java.math.BigInteger;

public class CSRFTokenUtil {
	private static final SecureRandom random = new SecureRandom();

	public static String generateToken() {
		return new BigInteger(130, random).toString(32);
	}
}
