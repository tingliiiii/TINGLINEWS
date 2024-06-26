package com.example.demo.security;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

// https://github.com/settings/developers

public class OAuth2Util {

	public final static String CLIENT_ID = "Ov23liDtaaE6SzdeiYZu";
	private final static String CLIENT_SECRET = "6763e0c128703044de100df890410311700670bb";
	public final static String REDIRECT_URI = "http://172.20.10.5:8080/tinglinews/callback/github";

	// GitHub 的 OAuth 2.0 授權端點 (Authorization Endpoint)
	// 用戶同意，GitHub 會將他們重定向回應用程序指定的 redirect_uri，並附帶一個授權碼（code）作為參數。
	public final static String AUTH_URL = "https://github.com/login/oauth/authorize" 
			+ "?client_id=" + CLIENT_ID
			+ "&redirect_uri=" + REDIRECT_URI;

	// GitHub 的 OAuth 2.0 令牌端點 (Token Endpoint)
	// 一旦應用程序從 GitHub 獲得授權碼，將呼叫這個端點以交換該授權碼為一個訪問令牌 (access token)
	// 這個訪問令牌將允許應用程序訪問用戶的 GitHub 資源，直到令牌過期或被撤銷
	private static final String ACCESS_TOKEN_URL = "https://github.com/login/oauth/access_token";

	/**
	 * 根據提供的 GitHub 授權碼來獲取訪問令牌 (access token)。
	 * 
	 * @param code 從 GitHub 授權後返回的授權碼。
	 * @return 返回從 GitHub 獲取的訪問令牌。該令牌用於訪問受保護的 GitHub API 資源。
	 * @throws IOException 如果在發送 HTTP 請求或接收 HTTP 回應時出現問題，則會拋出此異常。
	 * @throws InterruptedException
	 */
	public static String getGitHubAccessToken(String code) throws IOException, InterruptedException {
		// 準備請求參數
		String params = "client_id=" + CLIENT_ID + "&client_secret=" + CLIENT_SECRET + "&code=" + code
				+ "&redirect_uri=" + REDIRECT_URI;
		return sendPostRequest(ACCESS_TOKEN_URL, params);
	}

	/**
	 * 使用 POST 方法發送請求到給定的 URL，並回傳回應內容。
	 *
	 * @param targetURL     要發送請求的 URL。
	 * @param urlParameters POST 方法的參數。
	 * @return 伺服器的回應內容。
	 * @throws InterruptedException
	 */
	private static String sendPostRequest(String targetURL, String urlParameters)
			throws IOException, InterruptedException {
		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder().uri(URI.create(targetURL))
				.header("Content-Type", "application/x-www-form-urlencoded")
				.POST(HttpRequest.BodyPublishers.ofString(urlParameters)).build();

		HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
		return response.body();
	}
	
	/**
	 * 解析 access_token 從 GitHub 的 OAuth 回應。
	 *
	 * @param response 從 GitHub OAuth 服務獲得的回應內容。
	 * @return 解析出來的 access_token，如果不存在，則返回 null。
	 */
	public static String parseAccessToken(String response) {
		String[] pairs = response.split("&");
		for (String pair : pairs) {
			String[] keyValue = pair.split("=");
			if (keyValue[0].equals("access_token")) {
				return keyValue[1];
			}
		}
		return null;
	}

	/**
	 * 使用提供的訪問令牌調用 GitHub API 以獲取用戶資訊。
	 * 
	 * @param accessToken OAuth 2.0 訪問令牌
	 * @return GitHub 用戶的 JSON 表示形式
	 * @throws IOException 如果在訪問 API 時發生問題
	 */
	public static String getUserInfoFromGitHub(String accessToken) throws IOException {
		// GitHub API 的 URL 用於獲取用戶資訊
		String apiUrl = "https://api.github.com/user";
		HttpURLConnection apiConn = (HttpURLConnection) new URL(apiUrl).openConnection();

		// 設置請求方法和添加授權頭部
		apiConn.setRequestMethod("GET");
		apiConn.setRequestProperty("Authorization", "Bearer " + accessToken);

		// 讀取響應
		StringBuffer apiResponse = new StringBuffer();
		try (BufferedReader apiIn = new BufferedReader(new InputStreamReader(apiConn.getInputStream(), "UTF-8"));) {
			String apiLine;
			while ((apiLine = apiIn.readLine()) != null) {
				apiResponse.append(apiLine);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse.toString();
	}
}
