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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Tag(name = "News API")
@RestController
@RequestMapping("/news")
public class NewsController {

	@Autowired
	private NewsService newsService;
	
	// 搜尋全部新聞
	@Operation(summary = "查看所有已公開新聞")
	@GetMapping
	public ResponseEntity<ApiResponse<List<News>>> findAllNews() {
		try {
			List<News> newsList = newsService.findAllNewsForFront();
			ApiResponse<List<News>> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), newsList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error(e.getMessage());
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
	
	@Operation(summary = "按照標籤查看新聞")
	@GetMapping("/list/{tagId}")
	public ResponseEntity<ApiResponse<List<News>>> findNewsByTag(@PathVariable Integer tagId) {
		try {
			List<News> newsList = newsService.findNewsByTagId(tagId);
			ApiResponse<List<News>> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), newsList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error(e.getMessage());
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
	
	@Operation(summary = "查看單篇新聞")
	@GetMapping("/{newsId}")
	public ResponseEntity<ApiResponse<NewsDtoForFront>> findNewsById(@PathVariable Integer newsId) {
		try {
			NewsDtoForFront news = newsService.getNewsByIdForFront(newsId);
			ApiResponse<NewsDtoForFront> apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error(e.getMessage());
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}	
	}
	
}
