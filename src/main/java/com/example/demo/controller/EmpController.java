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
	public ResponseEntity<ApiResponse<List<UserDto>>> findAllUser(){
		List<UserDto> users = userService.findAllUserDtos();
		ApiResponse apiResponse = new ApiResponse<>(true, "query users success", users);
		return ResponseEntity.ok(apiResponse);	
	}
	
	// 後台：刪除使用者
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<User>> deleteUser(@PathVariable("userId") Integer userId) {
		User user = null;
		try {
			// 查詢該 user 是否存在
			user = userService.getUserById(userId);
			// 刪除
			Boolean state = userService.deleteUser(userId);
			String message = state ? "success" : "fail";
			// 回應資料
			ApiResponse apiResponse = new ApiResponse<>(state, "delete " + message, user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), user);
			return ResponseEntity.ok(apiResponse);
		}
	}


	// 後台：網頁內容管理介面
	@GetMapping("/news")
	public ResponseEntity<ApiResponse<List<NewsDto>>> findAllNews(){
		List<NewsDto> news = newsService.findAllNews();
		ApiResponse apiResponse = new ApiResponse<>(true, "query news success", news);
		return ResponseEntity.ok(apiResponse);	
	}
	
	// 新增文章時的標籤選項
	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<Tag>>> findAllTags(){
		List<Tag> tags = newsService.findAllTags();
		ApiResponse apiResponse = new ApiResponse<>(true, "query tags success", tags);
		return ResponseEntity.ok(apiResponse);	
	}

	// 新增文章
	@PostMapping("/post")
	public ResponseEntity<ApiResponse<News>> post(@RequestBody News news){
		Boolean state = newsService.postNews(news);
		ApiResponse<News> apiResponse = new ApiResponse<>(state, "post success", news);
		return ResponseEntity.ok(apiResponse);
	}


}
