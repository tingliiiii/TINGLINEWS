package com.example.demo.controller;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.NewsDtoForBack;
import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.dto.TopTagsByFavorites;
import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.Journalist;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;
import com.example.demo.model.response.ApiResponse;
import com.example.demo.model.response.StatusMessage;
import com.example.demo.service.FunctionService;
import com.example.demo.service.NewsService;
import com.example.demo.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Admin API")
@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;

	@Autowired
	private NewsService newsService;

	@Autowired
	private FunctionService functionService;

	// 後台：使用者管理介面
	@Operation(summary = "查看所有使用者")
	@GetMapping("/users")
	public ResponseEntity<ApiResponse<List<UserAdminDto>>> findAllUser() {
		return handleServiceCall(() -> {
			List<UserAdminDto> userList = userService.findAllUserAdminDtos();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), userList);
		});
	}

	// 後台：刪除使用者
	@Operation(summary = "刪除使用者")
	@DeleteMapping("/users/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Integer userId) {
		return handleServiceCall(() -> {
			Boolean state = userService.removeUser(userId);
			String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
			return new ApiResponse<>(state, message, null);
		});
	}

	// 修改使用者權限的選項
	@Operation(summary = "查詢所有權限選項")
	@GetMapping("/authorities")
	public ResponseEntity<ApiResponse<List<Authority>>> getAuthority() {
		return handleServiceCall(() -> {
			List<Authority> authorities = userService.getAllAuthorities();
			String message = (authorities != null && !authorities.isEmpty()) ? StatusMessage.查詢成功.name()
					: StatusMessage.查無資料.name();
			return new ApiResponse<>(true, message, authorities);
		});
	}

	// 修改使用者權限
	@Operation(summary = "修改使用者權限（僅限主管和管理員）")
	@PatchMapping("/users/{userId}/authority")
	public ResponseEntity<ApiResponse<Void>> updateUserAuthority(@PathVariable Integer userId,
			@RequestBody Map<String, Object> map) {
		// System.out.println(map);
		return handleServiceCall(() -> {
			Integer authorityId = Integer.valueOf(map.get("authorityId").toString());
			Boolean state = userService.updateUserAuthority(userId, authorityId);
			String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
			return new ApiResponse<>(state, message, null);
		});
	}

	// 後台：網頁內容管理介面
	@Operation(summary = "查看所有新聞")
	@GetMapping("/news")
	public ResponseEntity<ApiResponse<List<NewsDtoForBack>>> findAllNews() {
		return handleServiceCall(() -> {
			List<NewsDtoForBack> news = newsService.findAllNewsForBack();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
		});
	}

	// 新增文章
	@Operation(summary = "新增新聞（僅限編輯以上員工）")
	@PostMapping("/news")
	public ResponseEntity<ApiResponse<Void>> postNews(@RequestBody News news) {
		return handleServiceCall(() -> {
			Boolean state = newsService.postNews(news);
			String message = state ? StatusMessage.新增成功.name() : StatusMessage.新增失敗.name();
			return new ApiResponse<>(state, message, null);
		});
	}

	// 新增文章時的標籤選項
	@Operation(summary = "查詢所有標籤選項")
	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<Tag>>> findAllTags() {
		return handleServiceCall(() -> {
			List<Tag> tags = newsService.findAllTags();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), tags);
		});
	}

	// 新增文章時的記者選項
	@Operation(summary = "查詢所有記者")
	@GetMapping("/journalists")
	public ResponseEntity<ApiResponse<List<Journalist>>> findAllJournalists() {
		return handleServiceCall(() -> {
			List<Journalist> journalists = newsService.findAllJournalists();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), journalists);
		});
	}

	// 查看單篇文章（為了修改）
	@Operation(summary = "查詢單篇報導")
	@GetMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> getNews(@PathVariable Integer newsId) {
		return handleServiceCall(() -> {
			News news = newsService.getNewsById(newsId);
			Boolean state = news != null;
			String message = state ? StatusMessage.查詢成功.name() : StatusMessage.查無資料.name();
			return new ApiResponse<>(state, message, news);
		});
	}

	// 修改文章
	@Operation(summary = "修改新聞（僅限編輯以上員工）")
	@PutMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<Void>> updateNews(@PathVariable Integer newsId, @RequestBody News news) {
		return handleServiceCall(() -> {
			Boolean state = newsService.updateNews(newsId, news);
			String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
			return new ApiResponse<>(state, message, null);
		});
	}

	@Operation(summary = "發布或取消發布報導（僅限編輯以上員工）")
	@PatchMapping("/news/{newsId}/publish")
	public ResponseEntity<ApiResponse<Void>> publish(@PathVariable Integer newsId,
			@RequestBody Map<String, Object> map) {
		// System.out.println(map);
		return handleServiceCall(() -> {
			Boolean isPublic = (Boolean) map.get("public");
			Boolean state = newsService.publishNews(newsId, isPublic);
			String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
			return new ApiResponse<>(state, message, null);
		});
	}

	@Operation(summary = "查詢新聞收藏數排行榜")
	@GetMapping("/statistic/topsavednews")
	public ResponseEntity<ApiResponse<List<TopNewsByFavorites>>> getTopSavedNews() {
		return handleServiceCall(() -> {
			List<TopNewsByFavorites> topSavedNews = functionService.getTopNewsByFavorites();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), topSavedNews);
		});
	}

	@Operation(summary = "查詢新聞收藏數記者排行榜")
	@GetMapping("/statistic/topjournalists")
	public ResponseEntity<ApiResponse<List<TopJournalistsByFavorites>>> getTopJournalists() {
		return handleServiceCall(() -> {
			List<TopJournalistsByFavorites> topJournalists = functionService.getTopJournalistsByFavorites();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), topJournalists);
		});
	}
	
	@Operation(summary = "查詢新聞收藏數標籤排行榜")
	@GetMapping("/statistic/toptags")
	public ResponseEntity<ApiResponse<List<TopTagsByFavorites>>> getTopTags(){
		return handleServiceCall(()->{
			List<TopTagsByFavorites> topTagsByFavorites = functionService.getTopTagsByFavorites();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), topTagsByFavorites);
		});
	}

	private <T> ResponseEntity<ApiResponse<T>> handleServiceCall(ServiceCall<T> serviceCall) {
		try {
			ApiResponse<T> apiResponse = serviceCall.execute();
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			log.error(e.getMessage());
			ApiResponse<T> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
		}
	}

	@FunctionalInterface
	private interface ServiceCall<T> {
		ApiResponse<T> execute() throws Exception;
	}

}
