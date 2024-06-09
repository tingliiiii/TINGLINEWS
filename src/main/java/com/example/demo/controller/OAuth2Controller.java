package com.example.demo.controller;


import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo.security.OAuth2Util;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/callback/github")
public class OAuth2Controller {
	
	@GetMapping
	public String getToken(@RequestParam("code") String code, HttpSession session) throws Exception {
		session.setAttribute("code", code);
		// System.out.println("code: " + code);
		return "redirect:/callback/github.html";
		// return code;
	}

	@GetMapping("/exchange")
	public String exchangeToken(HttpSession session) throws Exception {

		String code = (String) session.getAttribute("code");
		// System.out.println("code: "+code);

		String token = OAuth2Util.getGitHubAccessToken(code);
		String accessToken = OAuth2Util.parseAccessToken(token);

		if (accessToken != null) {
			String userInfo = OAuth2Util.getUserInfoFromGitHub(accessToken);
			JSONObject userInfoObject = new JSONObject(userInfo);
			System.out.println(userInfoObject);
			System.out.println("login: " + userInfoObject.getString("login"));
			System.out.println("id: " + userInfoObject.getInt("id"));
			// System.out.println("email: " + userInfoObject.getInt("email"));

			session.setAttribute("loginStatus", true);
		} else {
			System.err.println("accessToken is null");
		}

		return "redirect:/user/profile.html";
	}

}
