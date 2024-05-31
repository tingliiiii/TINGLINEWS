package com.example.demo.controller;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.UserService;


@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> register(@RequestBody User user) {
		Integer userId = userService.addUser(user);
		if (userId != null) {
			user.setUserId(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, "Register success", user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(false, "Register failed", user);
		System.out.println(user);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<User>> login(@RequestBody Map<String ,Object> map) {
		// json 格式要用 @RequestBody 抓（最好是新增 class 抓，但如果用 Map 也可以）
		System.out.println(map);
		User user = userService.validateUser(map.get("userEmail") + "", map.get("userPassword") + "");
		if (user != null) {
			ApiResponse apiResponse = new ApiResponse<>(true, "Login success", user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse<User> apiResponse = new ApiResponse<>(false, "Login failed", user);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入註冊後資訊
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ApiResponse<User>> getUser(@PathVariable("userId") Integer userId) {
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

	// 登出
	/*
	@PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpSession session) {
        session.invalidate();
        ApiResponse<Void> apiResponse = new ApiResponse<>(true, "Logout success", null);
        return ResponseEntity.ok(apiResponse);
    }
	*/
	// 修改

}
