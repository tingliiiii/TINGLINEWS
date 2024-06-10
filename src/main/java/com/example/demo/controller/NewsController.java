package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.NewsDtoForFront;
import com.example.demo.model.po.News;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.StatusMessage;
import com.example.demo.service.NewsService;

@RestController
@RequestMapping("/news")
public class NewsController {

	@Autowired
	private NewsService newsService;
	
	// 搜尋全部新聞
	@GetMapping("/list/")
	public ResponseEntity<ApiResponse<List<News>>> findAllNews() {
		try {
			List<News> newsList = newsService.findAllNewsForFront();
			ApiResponse<List<News>> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), newsList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
	
	@GetMapping("/list/{tagId}")
	public ResponseEntity<ApiResponse<List<News>>> findNewsByTag(@PathVariable Integer tagId) {
		try {
			List<News> newsList = newsService.findNewsByTagId(tagId);
			ApiResponse<List<News>> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), newsList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
	
	@GetMapping("/{newsId}")
	public ResponseEntity<ApiResponse<NewsDtoForFront>> findNewsById(@PathVariable Integer newsId) {
		try {
			NewsDtoForFront news = newsService.getNewsByIdForFront(newsId);
			// System.out.println(news.getJournalists());
			ApiResponse<NewsDtoForFront> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
}
