package com.example.demo.controller;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.FavoriteDto;
import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Donation;
import com.example.demo.model.po.Favorite;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.StatusMessage;
import com.example.demo.security.CSRFTokenUtil;
import com.example.demo.security.CaptchaUtil;
import com.example.demo.security.OTPUtil;
import com.example.demo.service.FunctionService;
import com.example.demo.service.RedisService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "User API")
@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private RedisService redisService;

	private boolean validateCsrfToken(String sessionToken, String requestToken) {
		return sessionToken != null && requestToken != null && sessionToken.equals(requestToken);
	}

	// 註冊
	@Operation(summary = "註冊")
	@PostMapping("/register")
	public ResponseEntity<ApiResponse<UserAdminDto>> register(@RequestBody User user,
			@RequestHeader("X-CSRF-TOKEN") String requestCsrfToken, HttpSession session) {

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = session.getAttribute("csrfToken") + "";
		// System.out.println("login csrfToken: " + csrfToken);
		log.debug("register csrfToken: {}", csrfToken);

		// 從請求中獲取 CSRF Token 值
		// System.out.println("login requestCsrfToken: " + requestCsrfToken);
		log.debug("register requestCsrfToken: {}", requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (!validateCsrfToken(csrfToken, requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			log.warn("CSRF token validation failed for registration");
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		// CSRF Token 驗證通過，執行登入邏輯
		try {
			Integer userId = userService.createUser(user);
			UserAdminDto userDto = userService.getUserAdminDtoById(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.註冊成功.name(), userDto);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("Error during user registration", e);
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.註冊失敗.name(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 登入
	@Operation(summary = "登入")
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<UserAdminDto>> login(@RequestBody UserLoginDto dto,
			@RequestHeader("X-CSRF-TOKEN") String requestCsrfToken, HttpSession session) {

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = (String) session.getAttribute("csrfToken");
		// System.out.println("login csrfToken: " + csrfToken);
		log.debug("login csrfToken: {}", csrfToken);

		// 從請求中獲取的 CSRF Token 值
		// System.out.println("login requestCsrfToken: " + requestCsrfToken);
		log.debug("login requestCsrfToken: {}", requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (!validateCsrfToken(csrfToken, requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			log.warn("CSRF token validation failed for login");
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		// CSRF Token 驗證通過，執行登入邏輯
		try {
			User user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());
			if (user != null) {
				UserAdminDto userDto = userService.getUserAdminDtoById(user.getUserId());
				ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.登入成功.name(), userDto);
				return ResponseEntity.ok(apiResponse);
			} else {
				log.warn("Invalid login attempt for email: {}", dto.getUserEmail());
				ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
				return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiResponse);
			}
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("Error during user login", e);
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
		}
	}

	// CSRF Token
	@Operation(summary = "取得 CSRF Token")
	@GetMapping("/csrf-token")
	public ResponseEntity<ApiResponse<Map<String, String>>> getCsrfToken(HttpSession session) {

		String csrfToken = CSRFTokenUtil.generateToken();
		session.setAttribute("csrfToken", csrfToken);
		// System.out.println("getCsrfToken: " + csrfToken);
		log.debug("Generated csrfToken: {}", csrfToken);

		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("csrfToken", csrfToken);
		// System.out.println(tokenMap);
		log.debug("Token map: {}", tokenMap);

		ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), tokenMap);
		return ResponseEntity.ok(apiResponse);
	}

	@Operation(summary = "確認 Email 是否為會員")
	@PostMapping("/email")
	public ResponseEntity<ApiResponse<Integer>> checkEmailExists(@RequestBody Map<String, String> request){
		
		String email = request.get("email");
		// 先確認該 email 是否已經註冊為會員
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.查無資料.name(), null));
		}
		return ResponseEntity.ok(new ApiResponse<>(true, StatusMessage.查詢成功.name(), user.getUserId()));
	}
	
	// 發送郵件
	@Operation(summary = "發送 OTP 驗證碼 Email")
	@PostMapping("/otp")
	public ResponseEntity<ApiResponse<String>> sendEmail(@RequestBody Map<String, String> request,
			HttpSession session) {

		String email = request.get("email");
		String subject = "TINGLINEWS 電子信箱驗證";
		String otp = OTPUtil.generateOTP();
		String body = "<p>驗證碼：<b>" + otp + "&ensp;</b></p>"
				+ "<p><small>驗證碼將於30秒後失效，請儘速在驗證頁面完成驗證。若您並未要求此代碼，可以安全地忽略此電子郵件，可能有人誤輸入了您的電子郵件地址</small></p>";

		try {
			// 創建 MimeMessage
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper helper = new MimeMessageHelper(message, true);

			helper.setTo(email);
			helper.setSubject(subject);
			helper.setText(body, true); // 設置為 true 表示該郵件支持 HTML
			// 設置發件人顯示名稱和郵箱地址
			helper.setFrom(new InternetAddress("no-reply@tinglinews.com", "no-reply"));

			// 發送郵件
			mailSender.send(message);
			redisService.save(email, otp, 30, TimeUnit.SECONDS);

			ApiResponse apiResponse = new ApiResponse<>(true, "驗證碼已發送至信箱", null);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			// e.printStackTrace();
			log.error("Error sending OTP email", e);

			if (e.getMessage().contains("Invalid Addresses")) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, "無效電子信箱", null));
			}
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body(new ApiResponse<>(false, "驗證碼發送失敗", e.getMessage()));
		}
	}

	// 驗證OTP
	@Operation(summary = "驗證 OTP")
	@PostMapping("/otp/verify")
	public ResponseEntity<ApiResponse<Void>> verifyOTP(@RequestBody Map<String, String> request, HttpSession session) {
		// String sentTotp = (String) session.getAttribute("totp");
		// log.info("sentTotp: " + sentTotp);
		String email = request.get("email");
		String sentOtp = redisService.get(email);
		log.debug("sentOtp: " + sentOtp);
		String receivedOtp = request.get("otp");
		log.debug("receivedOtp: " + receivedOtp);

		if (sentOtp == null) {
			return ResponseEntity.ok(new ApiResponse<>(false, "驗證碼已過期", null));
		} else if (sentOtp.equals(receivedOtp)) {
			// session.setAttribute("verify", true);
			redisService.save("verify:" + email, "true", 10, TimeUnit.MINUTES); // 存儲驗證狀態，有效期10分鐘
			return ResponseEntity.ok(new ApiResponse<>(true, StatusMessage.驗證成功.name(), null));
		} else {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null));
		}
	}

	// 重設密碼
	@Operation(summary = "重設密碼")
	@PatchMapping("/password")
	public ResponseEntity<ApiResponse<String>> resetPassword(@RequestBody Map<String, String> request,
			HttpSession session) {

		// 確認 OTP 是否已經過驗證
		// Boolean verify = (Boolean) session.getAttribute("verify");
		String email = request.get("email");
		String verify = redisService.get("verify:" + email);
		if (verify == null || !"true".equals(verify)) {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null));
		}
		// 重設密碼邏輯
		String password = request.get("password");
		User user = userService.getUserByEmail(email);
		if (user == null) {
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.查無資料.name(), null));
		}
		try {
			Boolean state = userService.resetPassword(email, password);
			if (!state) {
				return ResponseEntity.ok(new ApiResponse<>(state, StatusMessage.更新失敗.name(), null));
			}
			return ResponseEntity.ok(new ApiResponse<>(state, StatusMessage.更新成功.name(), null));
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.ok(new ApiResponse<>(false, StatusMessage.更新失敗.name(), e.getMessage()));
		}

	}

	// 個人資訊 ============================================================

	// 登入註冊後資訊
	@Operation(summary = "查看使用者個人資訊")
	@GetMapping("/{userId}/profile")
	public ResponseEntity<ApiResponse<UserProfileDto>> getUser(@PathVariable Integer userId) {
		try {
			UserProfileDto userProfile = userService.getUserProfile(userId);
			ApiResponse<UserProfileDto> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), userProfile);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error("查詢使用者個人資訊失敗", e);
			ApiResponse<UserProfileDto> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 更新使用者個人資訊
	@Operation(summary = "更新使用者個人資訊")
	@PutMapping("/{userId}/profile")
	public ResponseEntity<ApiResponse<Void>> updateUser(@PathVariable Integer userId,
			@RequestBody UserProfileDto userProfile) {

		boolean state = userService.updateUserDetails(userId, userProfile);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, null);
		return ResponseEntity.ok(apiResponse);
	}

	// 贊助 ============================================================

	@Operation(summary = "取得 captcha 驗證碼")
	@GetMapping("/captcha")
	public ResponseEntity<ApiResponse<String>> getCaptcha(HttpSession session) {

		String captcha = CaptchaUtil.generateCaptchaCode();
		// System.out.println("captcha code: " + captcha);
		log.debug("captcha code: " + captcha);
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

	@Operation(summary = "驗證 captcha 驗證碼")
	@PostMapping("/captcha/verify")
	public ResponseEntity<ApiResponse<Void>> verifyCaptcha(@RequestBody Map<String, String> request,
			HttpSession session) {

		String sessionCaptcha = (String) session.getAttribute("captcha");
		// System.out.println("sessionCaptcha: " + sessionCaptcha);
		log.debug("sessionCaptcha: " + sessionCaptcha);
		String inputCaptcha = request.get("captcha");
		// System.out.println("inputCaptcha: " + inputCaptcha);
		log.debug("inputCaptcha: " + inputCaptcha);

		if (sessionCaptcha == null || inputCaptcha == null || !sessionCaptcha.equals(inputCaptcha)) {
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.驗證失敗.name(), null);
			return ResponseEntity.ok(apiResponse);
		}
		ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.驗證成功.name(), null);
		return ResponseEntity.ok(apiResponse);
	}

	@Operation(summary = "贊助")
	@PostMapping("/donations")
	public ResponseEntity<ApiResponse<Void>> addDonation(@RequestBody Donation donated) {

		Boolean state = false;
		String message;

		try {
			state = functionService.addDonation(donated);
			message = state ? StatusMessage.贊助成功.name() : StatusMessage.贊助失敗.name();
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("cannot be null")) {
				message = "欄位不可空白";
			} else {
				message = e.getMessage();
			}
		}
		ApiResponse apiResponse = new ApiResponse<>(state, message, null);
		return ResponseEntity.ok(apiResponse);
	}

	@Operation(summary = "取消贊助")
	@DeleteMapping("/donations/{donationId}")
	public ResponseEntity<ApiResponse<Boolean>> stopDonation(@PathVariable Integer donateId) {
		Boolean state = functionService.stopDonation(donateId);
		String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, state);
		return ResponseEntity.ok(apiResponse);
	}

	// 收藏 ============================================================

	@Operation(summary = "查看使用者收藏的文章")
	@GetMapping("/{userId}/favorites")
	public ResponseEntity<ApiResponse<FavoriteDto>> findUserFavoriteNews(@PathVariable Integer userId) {
		try {
			List<FavoriteDto> favoriteDtos = functionService.findFavoriteByUserId(userId);
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), favoriteDtos);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error("查詢使用者收藏文章失敗", e);
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);	
		}
	}

	@Operation(summary = "收藏")
	@PostMapping("/favorites")
	public ResponseEntity<ApiResponse<Void>> favorite(@RequestBody Favorite favorite) {
		Boolean state = false;
		String message = "發生錯誤：";
		try {
			state = functionService.addFavorite(favorite);
			message = state ? StatusMessage.收藏成功.name() : StatusMessage.收藏失敗.name();
		} catch (Exception e) {
			e.printStackTrace();
			if (e.getMessage().contains("favorites.unique_userid_and_newsid")) {
				message = "已收藏此篇報導";
			} else {
				message += e.getMessage();
			}
		}
		ApiResponse apiResponse = new ApiResponse<>(state, message, null);
		return ResponseEntity.ok(apiResponse);

	}

	@Operation(summary = "取消收藏")
	@DeleteMapping("/favorites/{favoriteId}")
	public ResponseEntity<ApiResponse<Boolean>> cancelFavorite(@PathVariable Integer favoriteId) {
		Boolean state = functionService.deleteFavorite(favoriteId);
		String message = state ? "已完成" : StatusMessage.刪除失敗.name();
		ApiResponse apiResponse = new ApiResponse<>(state, message, state);
		return ResponseEntity.ok(apiResponse);
	}
}
