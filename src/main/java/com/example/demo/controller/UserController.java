package com.example.demo.controller;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.example.demo.security.CSRFTokenUtil;
import com.example.demo.security.CaptchaUtil;
import com.example.demo.security.OAuth2Util;
import com.example.demo.security.OTPUtil;
import com.example.demo.service.FunctionService;
import com.example.demo.service.UserService;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private JavaMailSender mailSender;
	
	// 驗證OTP
	@PostMapping("/verifyOTP")
	public ResponseEntity<ApiResponse<String>> verifyOTP(@RequestBody Map<String, String> request, HttpSession session) {
		String sentOtp = (String) session.getAttribute("otp");
		String receivedOtp = request.get("otp");

		if (sentOtp != null && sentOtp.equals(receivedOtp)) {
			session.setAttribute("verify", true);
			return ResponseEntity.ok(new ApiResponse<>(true, StatusMessage.驗證成功.name(), null));
		} else {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null));
		}
	}

	// 重設密碼
	@PatchMapping("/resetPassword")
	public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request, HttpSession session) {

		// 確認 OTP 是否已經過驗證
		Boolean verify = (Boolean) session.getAttribute("verify");
		if (verify == null || !verify.equals(true)) {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null));
		}

		// 重設密碼邏輯
		String password = request.get("password");
		String userEmail = request.get("email");
		User user = userService.getUserByEmail(userEmail);
		if (user == null) {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.查無資料.name(), null));
		}

		Boolean state = false;
		try {
			state = userService.resetPassword(userEmail, password);
			if (!state) {
				return ResponseEntity.ok(new ApiResponse<>(state, StatusMessage.更新失敗.name(), null));
			}
			return ResponseEntity.ok(new ApiResponse<>(state, StatusMessage.更新成功.name(), null));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(new ApiResponse<>(state, StatusMessage.更新失敗.name(), e.getMessage()));
		}

	}

	// 發送郵件
	@PostMapping("/sendEmail")
	public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody Map<String, String> request, HttpSession httpSession) {

		String toEmail = request.get("toEmail");
		String subject = "TINGLINEWS 電子信箱驗證";
		String otp = OTPUtil.generateOTP();
		// String body = "驗證碼：" + otp;
		String body = "<p>驗證碼：<b>" + otp + "&ensp;</b></p>"
				+ "<p><small>若您並未要求此代碼，可以安全地忽略此電子郵件。可能有人誤輸入了您的電子郵件地址</small></p>";

		try {
			// 創建 MimeMessage
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(toEmail);
			helper.setSubject(subject);
			helper.setText(body, true); // 設置為 true 表示該郵件支持 HTML
			// 設置發件人顯示名稱和郵箱地址
			helper.setFrom(new InternetAddress("no-reply@tinglinews.com", "no-reply"));

			// 發送郵件
			mailSender.send(message);
			httpSession.setAttribute("otp", otp);

			ApiResponse apiResponse = new ApiResponse<>(true, "驗證碼已發送至信箱", null);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			if (e.getMessage().contains("Invalid Addresses")) {
				return ResponseEntity.ok(new ApiResponse<>(false, "無效電子信箱", "驗證碼發送失敗"));
			}
			e.printStackTrace();
			return ResponseEntity.ok(new ApiResponse<>(false, "驗證碼發送失敗", e.getMessage()));
		}
	}

	// CSRF Token
	@GetMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, String>>> getCsrfToken(HttpSession session) {
		String csrfToken = CSRFTokenUtil.generateToken();
		session.setAttribute("csrfToken", csrfToken);
		// System.out.println("getCsrfToken: " + csrfToken);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("csrfToken", csrfToken);
		ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), tokenMap);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody UserLoginDto dto,
			HttpServletRequest request, HttpSession session) {
		// Map<String ,Object> map
		// json 格式要用 @RequestBody 抓（通常是準備 DTO 定義傳入資料，但如果用 Map 也可以）
		// User user = userService.validateUser(map.get("userEmail") + "",
		// map.get("userPassword") + "");

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = session.getAttribute("csrfToken") + "";
		// System.out.println("login csrfToken: " + csrfToken);

		// 從請求中獲取 CSRF Token 值
		String requestCsrfToken = request.getHeader("X-CSRF-TOKEN");
		// System.out.println("login requestCsrfToken: " + requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (csrfToken == null || requestCsrfToken == null || !csrfToken.equals(requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		// CSRF Token 驗證通過，執行登入邏輯
		User user = null;
		try {
			user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());
			if (user != null) {
				Map<String, Object> userInfo = new HashMap<>();
				userInfo.put("userId", user.getUserId());
				userInfo.put("userName", user.getUserName());
				userInfo.put("userEmail", user.getUserEmail());
				ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.登入成功.name(), user);
				return ResponseEntity.ok(apiResponse);
			} else {
				ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
				return ResponseEntity.ok(apiResponse);
			}
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
			return ResponseEntity.ok(apiResponse);
		}

	}

	// 註冊
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<User>> register(@RequestBody User user, HttpServletRequest request,
			HttpSession session) {

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = session.getAttribute("csrfToken") + "";
		// System.out.println("login csrfToken: " + csrfToken);

		// 從請求中獲取 CSRF Token 值
		String requestCsrfToken = request.getHeader("X-CSRF-TOKEN");
		// System.out.println("login requestCsrfToken: " + requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (csrfToken == null || requestCsrfToken == null || !csrfToken.equals(requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			ApiResponse<User> apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		// CSRF Token 驗證通過，執行登入邏輯
		Integer userId = 0;
		try {
			userId = userService.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse<User> apiResponse = new ApiResponse<>(false, StatusMessage.註冊失敗.name(), user);
			return ResponseEntity.ok(apiResponse);
		}
		if (userId != 0) {
			user.setUserId(userId);
			ApiResponse<User> apiResponse = new ApiResponse<>(true, StatusMessage.註冊成功.name(), user);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse<User> apiResponse = new ApiResponse<>(false, StatusMessage.註冊失敗.name(), user);
		return ResponseEntity.ok(apiResponse);
	}

	// 登入註冊後資訊
	@GetMapping("/profile/{userId}")
	public ResponseEntity<ApiResponse<UserProfileDto>> getUser(@PathVariable Integer userId) {
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

	// 修改
	@PutMapping("/update/{userId}")
	public ResponseEntity<ApiResponse<User>> updateUser(@PathVariable Integer userId,
			@RequestBody UserProfileDto userProfile) {
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

	@GetMapping("/captcha")
	public ResponseEntity<ApiResponse<String>> getCaptcha(HttpSession session) {

		String captcha = CaptchaUtil.generateCaptchaCode();
		// System.out.println("captcha code: " + captcha);
		session.setAttribute("captcha", captcha);

		try {
			// 取得圖片資訊
			BufferedImage img = CaptchaUtil.getCaptchaImage(captcha);
			// 轉換為 base64 格式
			String base64Image = CaptchaUtil.imageToBase64(img);
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), base64Image);
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢失敗.name(), e.getMessage());
			return ResponseEntity.ok(apiResponse);
		}
	}

	@PostMapping("/captcha")
	public ResponseEntity<ApiResponse<String>> verifyCaptcha(@RequestBody Map<String, String> request,
			HttpSession session) {

		String sessionCaptcha = (String) session.getAttribute("captcha");
		String inputCaptcha = request.get("captcha");

		if (sessionCaptcha == null || inputCaptcha == null || !sessionCaptcha.equals(inputCaptcha)) {
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.驗證成功.name(), null);
		return ResponseEntity.ok(apiResponse);
	}

	@PostMapping("/donate")
	public ResponseEntity<ApiResponse<Donated>> addDonate(@RequestBody Donated donated, HttpSession session) {

		Boolean state = false;
		String message;

		try {
			state = functionService.addDonated(donated);
			message = state ? StatusMessage.贊助成功.name() : StatusMessage.贊助失敗.name();
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("cannot be null")) {
				message = "欄位不可空白";
			} else {
				message = e.getMessage();
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
		Boolean state = false;
		String message = "發生錯誤：";
		try {
			state = functionService.addSaved(saved);
			message = state ? StatusMessage.收藏成功.name() : StatusMessage.收藏失敗.name();
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("saved.unique_userid_and_newsid")) {
				message = "已收藏此篇報導";
			} else {
				message += e.getMessage();
			}
		}
		ApiResponse apiResponse = new ApiResponse<>(state, message, saved);
		return ResponseEntity.ok(apiResponse);

	}

	@DeleteMapping("/saved/{savedId}")
	public ResponseEntity<ApiResponse<Boolean>> cancelSaved(@PathVariable("savedId") Integer savedId) {
		Boolean state = functionService.deleteSaved(savedId);
		String message = state ? "已完成" : StatusMessage.刪除失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, state);
		return ResponseEntity.ok(apiResponse);
	}
}
