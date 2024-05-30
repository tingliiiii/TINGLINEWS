package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<List<User>>> profile(){
		List<User> users = userService.findAllUsers();
		ApiResponse apiResponse = new ApiResponse<>(true, "query success", users);
		return ResponseEntity.ok(apiResponse);
	}
	
	
	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDto>> addUser(@RequestBody User user) {
		Integer userId = userService.addUser(user);
		if(userId != null) {
			user.setUserId(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, "add success", user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(false, "add fail", user);
		System.out.println(user);
		return ResponseEntity.ok(apiResponse);
	}
	
	
	// 登入
	
	
	
	// 登出
	
	
	
	// 修改
	
}
