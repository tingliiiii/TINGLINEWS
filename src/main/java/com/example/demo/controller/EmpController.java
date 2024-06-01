package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.NewsDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;
import com.example.demo.model.po.User;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/emp")
public class EmpController {

	@Autowired
	private UserService userService;

	@Autowired
	private NewsService newsService;

	// 後台：使用者管理介面
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<UserDto>>> findAllUser() {
		List<UserDto> users = null;
		try {
			users = userService.findAllUserDtos();
			ApiResponse apiResponse = new ApiResponse<>(true, "query users success", users);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), users);
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
			String message = state ? "delete success" : "delete failed";
			ApiResponse apiResponse = new ApiResponse<>(state, message, user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), user);
			return ResponseEntity.ok(apiResponse);
		}
	}

	// 後台：網頁內容管理介面
	@GetMapping("/news")
	public ResponseEntity<ApiResponse<List<NewsDto>>> findAllNews() {
		List<NewsDto> news = null;
		try {
			news = newsService.findAllNews();
			ApiResponse apiResponse = new ApiResponse<>(true, "query news success", news);
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
			ApiResponse apiResponse = new ApiResponse<>(true, "query tags success", tags);
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
		String message = state ? "post success" : "post failed";
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);

	}

	// 找到單篇文章（為了修改）
	@GetMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> getNews(@PathVariable Integer newsId) {
		News news = newsService.getNewsById(newsId);
		Boolean state = news != null;
		String message = state ? "query success" : "query failed";
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
	}

	// 修改文章
	@PutMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> updateNews(@PathVariable Integer newsId, @RequestBody News news) {
		Boolean state = newsService.updateNews(newsId, news);
		String message = state ? "update success" : "update failed";
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
		
	}

}
