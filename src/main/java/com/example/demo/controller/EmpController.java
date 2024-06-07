package com.example.demo.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.NewsDtoForBack;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.StatusMessage;
import com.example.demo.security.CSRFTokenUtil;
import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/emp")
public class EmpController {

	@Autowired
	private UserService userService;

	@Autowired
	private NewsService newsService;

	// CSRF Token
	@GetMapping("/login")
	public ResponseEntity<Map<String, String>> getCsrfToken(HttpSession session) {
		String csrfToken = CSRFTokenUtil.generateToken();
		session.setAttribute("csrfToken", csrfToken);
		System.out.println("getCsrfToken: " + csrfToken);
		Map<String, String> tokenMap = new HashMap<>();
		tokenMap.put("csrfToken", csrfToken);
		return ResponseEntity.ok(tokenMap);
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody UserLoginDto dto, HttpServletRequest request,
			HttpSession session) {

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = session.getAttribute("csrfToken") + "";
		System.out.println("login csrfToken: " + csrfToken);

		// 從請求中獲取 CSRF Token 值
		String requestCsrfToken = request.getHeader("X-CSRF-TOKEN");
		System.out.println("login requestCsrfToken: " + requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (csrfToken == null || requestCsrfToken == null || !csrfToken.equals(requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			ApiResponse apiResponse = new ApiResponse<>(false, "CSRF Token 驗證失敗", null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		// CSRF Token 驗證通過，執行登入邏輯
		try {
			User user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());
			// 若驗證成功
			if (user != null) {
				UserDto userDto = userService.getUserDtoFromUserId(user.getUserId());
				ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.登入成功.name(), userDto);
				return ResponseEntity.ok(apiResponse);
			}
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 後台：使用者管理介面
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<UserDto>>> findAllUser() {

		try {
			List<UserDto> userList = userService.findAllUserDtos();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), userList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 後台：刪除使用者
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable("userId") Integer userId) {
		User user = null;
		try {
			user = userService.getUserById(userId);
			Boolean state = userService.deleteUser(userId);
			String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
			ApiResponse apiResponse = new ApiResponse<>(state, message, user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), user);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 修改使用者權限的選項
	@GetMapping("/authority")
	public ResponseEntity<ApiResponse<List<Authority>>> getAuthority() {
		List<Authority> authorities = userService.findAllAuthorities();
		ApiResponse apiResponse;
		if (authorities != null) {
			apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), authorities);
		} else {
			apiResponse = new ApiResponse<>(true, StatusMessage.查無資料.name(), authorities);
		}
		return ResponseEntity.ok(apiResponse);
	}

	// 修改使用者權限
	@PatchMapping("/authority/{userId}")
	public ResponseEntity<ApiResponse<UserDto>> updateUserAuthority(@PathVariable Integer userId,
			@RequestBody Map<String, Object> map) {
		System.out.println(map);
		try {
			String authorityIdString = map.get("authorityId") + "";
			Integer authorityId = Integer.valueOf(authorityIdString);
			Boolean state = userService.updateUserAuthority(userId, authorityId);
			String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
			UserDto dto = userService.getUserDtoFromUserId(userId);

			ApiResponse apiResponse = new ApiResponse<>(state, message, dto);
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), null);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 後台：網頁內容管理介面
	@GetMapping("/news")
	public ResponseEntity<ApiResponse<List<NewsDtoForBack>>> findAllNews() {
		List<NewsDtoForBack> news = null;
		try {
			news = newsService.findAllNewsForBack();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), news);
			return ResponseEntity.ok(apiResponse);
		}

	}

	// 新增文章時的標籤選項
	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<Tag>>> findAllTags() {
		List<Tag> tags = null;
		try {
			tags = newsService.findAllTags();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), tags);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), tags);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 新增文章
	@PostMapping("/post")
	public ResponseEntity<ApiResponse<News>> postNews(@RequestBody News news) {
		Boolean state = newsService.postNews(news);
		String message = state ? StatusMessage.新增成功.name() : StatusMessage.新增失敗.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);

	}

	// 找到單篇文章（為了修改）
	@GetMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> getNews(@PathVariable Integer newsId) {
		News news = newsService.getNewsById(newsId);
		Boolean state = news != null;
		String message = state ? StatusMessage.查詢成功.name() : StatusMessage.查無資料.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
	}

	// 修改文章
	@PutMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> updateNews(@PathVariable Integer newsId, @RequestBody News news) {
		Boolean state = newsService.updateNews(newsId, news);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
	}

	@PutMapping("/publish/{newsId}")
	public ResponseEntity<ApiResponse<Map>> publish(@PathVariable Integer newsId,
			@RequestBody Map<String, Object> map) {
		System.out.println(map);
		Boolean isPublic = (Boolean) map.get("public");
		Boolean state = newsService.publishNews(newsId, isPublic);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse<Map> apiResponse = new ApiResponse<>(state, message, map);
		return ResponseEntity.ok(apiResponse);
	}

}
