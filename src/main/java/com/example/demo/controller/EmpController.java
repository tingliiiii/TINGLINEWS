package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/emp")
public class EmpController {
	
	@Autowired
	private UserService userService;

	// 後台：使用者管理介面
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<User>>> findAllUser(){
		List<User> users = userService.findAllUsers();
		ApiResponse apiResponse = new ApiResponse<>(true, "query success", users);
		return ResponseEntity.ok(apiResponse);	
	}
	
	// 後台：刪除
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable("userId") Integer userId) {
		User user = null;
		try {
			// 查詢該 user 是否存在
			user = userService.getUserById(userId);
			// 刪除
			Boolean state = userService.deleteUser(userId);
			String message = state ? "success" : "fail";
			// 回應資料
			ApiResponse apiResponse = new ApiResponse<>(state, "delete " + message, user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), user);
			return ResponseEntity.ok(apiResponse);
		}
		
	}
}
