package com.example.demo.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// @WebFilter("/emp/*")
public class LoginFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		System.out.println("LoginFilter");
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		HttpSession session = req.getSession();
		Object isLogin = session.getAttribute("isLogin");
		System.out.println(isLogin);
		// chain.doFilter(request, response);
		
		if ("true".equals(isLogin)) {
			chain.doFilter(request, response);
		} else {
			resp.sendRedirect("/tinglinews/login.html");
			return;
		}
		

		
		/*
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		HttpSession session = req.getSession();
		
		// 判斷使用者是否已經登入 ?
		Object loginStatue = session.getAttribute("loginStatue");
		
		if(loginStatue != null && Boolean.valueOf(loginStatue+"")) {
			// 放行
			chain.doFilter(request, response);
		} else {
			// 驗證 username & password
			String userEmail = req.getParameter("userEmail");
			String password = req.getParameter("password");
			// 判斷是否有輸入 userEmail
			if(userEmail == null || userEmail.trim().length() == 0) {
				
				return;
			}
			
			// TODO csrfToken
			
			// 是否有此 userEmail ?
			Map<String, String> user = users.get(username);
			
			if(user == null) {
				System.out.println("無此使用者");
				resp.sendRedirect("/login");
				return;
			}
			// 判斷 password
			// 得到使用者的 hash 與 salt
			String hash = user.get("hash"); // 使用者的 hash
			byte[] salt = WebKeyUtil.hexStringToByteArray(user.get("salt")); // 使用者的 salt
			
			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.reset(); // 重制
				messageDigest.update(salt); // 加鹽
				byte[] inputHashedBytes = messageDigest.digest(password.getBytes());
				// 根據使用者輸入的 password 與已知的 salt 來產出 comparedHash
				String comparedHash = WebKeyUtil.bytesToHexString(inputHashedBytes);
				// 比較 comparedHash(運算的) 與 hash(已儲存的) 是否相等
				if(comparedHash.equals(hash)) {
					// 儲存登入狀態
					session.setAttribute("loginStatue", true);
					// 放行
					chain.doFilter(request, response);
					return;
				} else {
					System.out.println("登入失敗");
					resp.sendRedirect("/login");
					return;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				resp.sendRedirect("/login");
			}
			*/
			

	}

}
