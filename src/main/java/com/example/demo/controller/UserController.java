package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> addUser(@RequestBody User user) {
		Integer userId = userService.addUser(user);
		if(userId != null) {
			user.setUserId(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, "Register success", user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(false, "Register failed", user);
		System.out.println(user);
		return ResponseEntity.ok(apiResponse);
	}
	
	@GetMapping("/register/{userId}")
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable("userId")Integer userId) {
		try {
			User user = userService.getUserById(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, "query success", user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}
	
	// 登入
	
	
	
	// 登出
	
	
	
	// 修改
	
}
