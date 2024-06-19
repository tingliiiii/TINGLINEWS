package com.example.demo.controller;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.security.OAuth2Util;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "OAuth2 API")
@Controller
@RequestMapping("/callback")
public class OAuth2Controller {

	@Autowired
	private UserService userService;

	@GetMapping("/github")
	public String getToken(@RequestParam("code") String code, HttpSession session) {
		session.setAttribute("code", code);
		// System.out.println("Received code: " + code);
		log.info("Received code: " + code);
		return "redirect:/callback/github.html";
	}

	@Operation(summary = "使用 GitHub 授權碼交換訪問令牌")
	@GetMapping("/github/exchange")
	@ResponseBody
	public ApiResponse<UserAdminDto> exchangeToken(HttpSession session) {

		try {
			String code = (String) session.getAttribute("code");
			// System.out.println("code: " + code);
			log.info("Authorization code: " + code);
			if (code == null) {
				return new ApiResponse<>(false, "Missing authorization code", null);
			}

			String token = OAuth2Util.getGitHubAccessToken(code);
			// System.out.println(token);
			log.info("Access token: " + token);
			if (token == null) {
				return new ApiResponse<>(false, "Failed to obtain access token", null);
			}

			String accessToken = OAuth2Util.parseAccessToken(token);
			if (accessToken == null) {
				log.warn("Failed to parse access token");
				return new ApiResponse<>(false, "Failed to parse access token", null);
			}

			String userInfo = OAuth2Util.getUserInfoFromGitHub(accessToken);
			JSONObject userInfoObject = new JSONObject(userInfo);
			// System.out.println(userInfoObject);
			// System.out.println("login: " + userInfoObject.getString("login"));
			// System.out.println("id: " + userInfoObject.getInt("id"));
			log.info("UserInfoObject: " + userInfoObject);
			log.info("Login: " + userInfoObject.getString("login"));
			log.info("Id: " + userInfoObject.getInt("id"));

			String login = userInfoObject.getString("login");
			Integer providerUserId = userInfoObject.getInt("id");
			String email = userInfoObject.optString("email", providerUserId + "@github.com"); // 有些 GitHub 用戶沒有 email
			String name = userInfoObject.optString("name", login);

			User user = userService.findOrCreateUser("github", providerUserId, email, name);
			Integer userId = user.getUserId();
			UserAdminDto userDto = userService.getUserAdminDtoById(userId);

			return new ApiResponse<>(true, "success", userDto);
		} catch (Exception e) {
			e.printStackTrace();
			return new ApiResponse<>(false, "Internal server error", null);

		}
	}

}
