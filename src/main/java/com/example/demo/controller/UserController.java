package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserDto;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;

@RestController
@RequestMapping("/tinglinews/user")
public class UserController {
	
	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserDto>> addUser(@RequestBody User user) {
		/* Integer userId = userService.addUserAndGetId(user);
		if(userId != null) {
			user.setId(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, "add success", user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(false, "add fail", user);
		*/
		ApiResponse apiResponse = new ApiResponse<>(true, "add success", user);
		System.out.println(user);
		return ResponseEntity.ok(apiResponse);
	}
	
	
	// 登入
	
	
	
	// 登出
	
	
	
	// 修改
	
}
