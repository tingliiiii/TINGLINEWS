package com.example.demo;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.demo.service.RedisService;

@SpringBootTest
class SpringbootWebApplicationTests {
	
	@Autowired
	private RedisService redisService;

	@Test
	void contextLoads() {
		redisService.save("Hello2", "World", 5, TimeUnit.SECONDS);
	}

}
