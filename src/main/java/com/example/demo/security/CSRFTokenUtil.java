package com.example.demo.security;

import java.security.SecureRandom;
import java.util.UUID;

public class CSRFTokenUtil {
	private static final SecureRandom random = new SecureRandom();

	// 生成 CSRF 令牌
	public static String generateToken() {
		// 使用 UUID 來生成唯一的字串作為 CSRF 令牌
		return UUID.randomUUID().toString();
	}
}
