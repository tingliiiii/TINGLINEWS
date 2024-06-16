package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

	 // 跨域請求
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") // 對所有路徑下的請求進行跨域設定
				.allowedOrigins("http://localhost:8080", "http://172.20.10.5:8080") // 允許來自特定來源的跨域請求
				.allowedMethods("*") // 允許的請求方法
				.allowedHeaders("*") // 允許所有標頭
				.allowCredentials(true); // 允許跨域請求中包含認證信息
	}
	
}
