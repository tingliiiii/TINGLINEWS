package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Donated;
import com.example.demo.model.po.Saved;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.StatusMessage;
import com.example.demo.service.FunctionService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

// TODO unique_userid_and_newsid 出現時報錯：這篇報導已被收藏

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FunctionService functionService;

	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> register(@RequestBody User user, HttpSession session) {
		Integer userId = userService.addUser(user);
		if (userId != null) {
			user.setUserId(userId);
			session.setAttribute("user", user);
			ApiResponse<User> apiResponse = new ApiResponse<>(true, StatusMessage.註冊成功.name(), user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse<User> apiResponse = new ApiResponse<>(false, StatusMessage.註冊失敗.name(), user);
		System.out.println(user);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<User>> login(@RequestBody UserLoginDto dto, HttpSession session) {
		// Map<String ,Object> map
		// json 格式要用 @RequestBody 抓（通常是準備 DTO 定義傳入資料，但如果用 Map 也可以）
		System.out.println(dto);
		// User user = userService.validateUser(map.get("userEmail") + "",
		// map.get("userPassword") + "");
		User user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());

		if (user != null) {
			session.setAttribute("user", user);
			System.out.println(session.getAttribute("userId"));
			ApiResponse<User> apiResponse = new ApiResponse<>(true, StatusMessage.登入成功.name(), user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse<User> apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), user);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入註冊後資訊
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ApiResponse<UserProfileDto>> getUser(@PathVariable("userId") Integer userId) {
		try {
			UserProfileDto userProfile = userService.getUserProfile(userId);
			ApiResponse<UserProfileDto> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), userProfile);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse<UserProfileDto> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 登出
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<String>> logout(HttpSession session) {
		session.invalidate();
		ApiResponse<String> apiResponse = new ApiResponse<>(true, StatusMessage.登出成功.name(), null);
		return ResponseEntity.ok(apiResponse);
	}

	// 修改
	@PutMapping("/update")
	public ResponseEntity<ApiResponse<User>> updateUser(@RequestBody UserProfileDto userProfile, HttpSession session) {
		Integer userId = (Integer) session.getAttribute("userId");
		User user = userService.getUserById(userId);
		user.setUserName(userProfile.getUserName());
		user.setUserEmail(userProfile.getUserEmail());
		user.setBirthday(userProfile.getBirthday());
		user.setGender(userProfile.getGender());
		user.setPhone(userProfile.getPhone());
		boolean state = userService.updateUser(userId, user);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse<User> apiResponse = new ApiResponse<>(state, message, user);
		return ResponseEntity.ok(apiResponse);
	}

	// 贊助 ============================================================

	@PostMapping("/donate")
	public ResponseEntity<ApiResponse<Donated>> addDonate(@RequestBody Donated donated) {
		Boolean state = false;
		String message = "發生錯誤 ";
		try {
			state = functionService.addDonated(donated);
			message = state ? StatusMessage.贊助成功.name() : StatusMessage.贊助失敗.name();
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("cannot be null")) {
				message += "欄位不可空白";
			} else {
				message += e.getMessage();
			}
		}
		ApiResponse apiResponse = new ApiResponse<>(state, message, donated);
		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping("/donate/{donateId}")
	public ResponseEntity<ApiResponse<Boolean>> stopDonated(@PathVariable("donateId") Integer donateId) {
		Boolean state = functionService.stopDanted(donateId);
		String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, state);
		return ResponseEntity.ok(apiResponse);
	}

	// 收藏 ============================================================

	@PostMapping("/saved")
	public ResponseEntity<ApiResponse<Donated>> saved(@RequestBody Saved saved) {
		Boolean state = functionService.addSaved(saved);
		String message = state ? StatusMessage.收藏成功.name() : StatusMessage.收藏失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, saved);
		return ResponseEntity.ok(apiResponse);
	}

	@DeleteMapping("/saved/{savedId}")
	public ResponseEntity<ApiResponse<Boolean>> cancelSaved(@PathVariable("savedId") Integer savedId) {
		Boolean state = functionService.deleteSaved(savedId);
		String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, state);
		return ResponseEntity.ok(apiResponse);
	}
}
