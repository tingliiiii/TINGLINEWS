package com.example.demo.controller;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.security.OAuth2Util;
import com.example.demo.service.UserService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/callback")
public class OAuth2Controller {
	
	private static final Logger logger = LoggerFactory.getLogger(OAuth2Controller.class);
	
	@Autowired
	private UserService userService;

	@GetMapping("/github")
	public String getToken(@RequestParam("code") String code, HttpSession session) {
		session.setAttribute("code", code);
		logger.info("Received code: {}", code);
		System.out.println("Received code: " + code);
		return "redirect:/callback/github.html";
	}

	@GetMapping("/github/exchange")
	public ResponseEntity<ApiResponse<Integer>> exchangeToken(HttpSession session) {

		try {
			String code = (String) session.getAttribute("code");
			logger.info("Authorization code: {}", code);
			System.out.println("code: " + code);
			if (code == null) {
				return ResponseEntity.ok(new ApiResponse<>(false, "Missing authorization code", null));
			}

			String token = OAuth2Util.getGitHubAccessToken(code);
			if (token == null) {
				return ResponseEntity.ok(new ApiResponse<>(false, "Failed to obtain access token", null));
			}

			String accessToken = OAuth2Util.parseAccessToken(token);
			if (accessToken == null) {
				return ResponseEntity.ok(new ApiResponse<>(false, "Failed to parse access token", null));
			}

			String userInfo = OAuth2Util.getUserInfoFromGitHub(accessToken);
			JSONObject userInfoObject = new JSONObject(userInfo);
			logger.info("UserInfo: {}", userInfoObject);
			System.out.println(userInfoObject);
			System.out.println("login: " + userInfoObject.getString("login"));
			System.out.println("id: " + userInfoObject.getInt("id"));
			
			String login = userInfoObject.getString("login");
			Integer providerUserId = userInfoObject.getInt("id");
            String email = userInfoObject.optString("email", providerUserId + "@github.com"); // 有些 GitHub 用戶沒有 email
            String name = userInfoObject.optString("name", login);
            
            User user = userService.findOrCreateUser("github", providerUserId, email, name);
            Integer userId = user.getUserId();

			return ResponseEntity.ok(new ApiResponse<Integer>(true, "success", userId));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Error during GitHub OAuth exchange", e);
			return ResponseEntity.ok(new ApiResponse<>(false, "Internal server error", null));

		}
	}

}
