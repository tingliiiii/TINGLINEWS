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
import com.example.demo.model.dto.TopJournalists;
import com.example.demo.model.dto.TopSavedNews;
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


@io.swagger.v3.oas.annotations.tags.Tag(name = "Employee API")
@RestController
@RequestMapping("/emp")
public class EmpController {

	@Autowired
	private UserService userService;

	@Autowired
	private NewsService newsService;
	
	@Autowired
	private FunctionService functionService;

	/*
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<UserAdminDto>> login(@RequestBody UserLoginDto dto, 
			@RequestHeader("X-CSRF-TOKEN") String requestCsrfToken, 
			HttpSession session) {

		// 從 HttpServletRequest 中獲取 CsrfToken
		String csrfToken = session.getAttribute("csrfToken") + "";
		System.out.println("login csrfToken: " + csrfToken);

		// 從請求中獲取的 CSRF Token 值
		System.out.println("login requestCsrfToken: " + requestCsrfToken);

		// 檢查 CSRF Token 是否存在並且與請求中的值相符
		if (csrfToken == null || requestCsrfToken == null || !csrfToken.equals(requestCsrfToken)) {
			// CSRF Token 驗證失敗，返回錯誤狀態碼或錯誤訊息
			ApiResponse apiResponse = new ApiResponse<>(false, "CSRF Token 驗證失敗", null);
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
		}

		return handleServiceCall(() -> {
            User user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());
            if (user != null) {
                UserAdminDto userDto = userService.getUserAdminDtoById(user.getUserId());
                return new ApiResponse<>(true, StatusMessage.登入成功.name(), userDto);
            }
            return new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
        });
        
		// CSRF Token 驗證通過，執行登入邏輯
		
		try {
			User user = userService.validateUser(dto.getUserEmail(), dto.getUserPassword());
			// 若驗證成功
			if (user != null) {
				UserAdminDto userDto = userService.getUserAdminDtoById(user.getUserId());
				ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.登入成功.name(), userDto);
				return ResponseEntity.ok(apiResponse);
			}
			ApiResponse apiResponse = new ApiResponse<>(false, StatusMessage.登入失敗.name(), null);
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			e.printStackTrace();
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
		
	}*/

	// 後台：使用者管理介面
	@GetMapping("/user")
	public ResponseEntity<ApiResponse<List<UserAdminDto>>> findAllUser() {
		return handleServiceCall(() -> {
			List<UserAdminDto> userList = userService.findAllUserAdminDtos();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), userList);
		});
		/*
		try {
			List<UserAdminDto> userList = userService.findAllUserAdminDtos();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), userList);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.getMessage(), null);
			return ResponseEntity.ok(apiResponse);
		}
		*/

	}

	// 後台：刪除使用者
	@DeleteMapping("/user/{userId}")
	public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("userId") Integer userId) {
		 return handleServiceCall(() -> {
	            Boolean state = userService.removeUser(userId);
	            String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
	            return new ApiResponse<>(state, message, null);
	        });
		/*
		User user = null;
		try {
			user = userService.getUserById(userId);
			Boolean state = userService.deleteUser(userId);
			String message = state ? StatusMessage.刪除成功.name() : StatusMessage.刪除失敗.name();
			ApiResponse apiResponse = new ApiResponse<>(state, message, user);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), user);
			return ResponseEntity.ok(apiResponse);
		} 
		*/
	}

	// 修改使用者權限的選項
	@GetMapping("/authority")
	public ResponseEntity<ApiResponse<List<Authority>>> getAuthority() {
		return handleServiceCall(() -> {
            List<Authority> authorities = userService.getAllAuthorities();
            String message = (authorities != null && !authorities.isEmpty()) ? StatusMessage.查詢成功.name() : StatusMessage.查無資料.name();
            return new ApiResponse<>(true, message, authorities);
        });
		/*
		List<Authority> authorities = userService.findAllAuthorities();
		ApiResponse apiResponse;
		if (authorities != null) {
			apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), authorities);
		} else {
			apiResponse = new ApiResponse<>(true, StatusMessage.查無資料.name(), authorities);
		}
		return ResponseEntity.ok(apiResponse);
		*/
	}

	// 修改使用者權限
	@PatchMapping("/authority/{userId}")
	public ResponseEntity<ApiResponse<Void>> updateUserAuthority(@PathVariable Integer userId,
			@RequestBody Map<String, Object> map) {
		// System.out.println(map);
		  return handleServiceCall(() -> {
	            Integer authorityId = Integer.valueOf(map.get("authorityId").toString());
	            Boolean state = userService.updateUserAuthority(userId, authorityId);
	            String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
	            return new ApiResponse<>(state, message, null);
	        });
		  /*
		try {
			String authorityIdString = map.get("authorityId") + "";
			Integer authorityId = Integer.valueOf(authorityIdString);
			Boolean state = userService.updateUserAuthority(userId, authorityId);
			String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
			UserAdminDto dto = userService.getUserAdminDtoFromUserId(userId);

			ApiResponse apiResponse = new ApiResponse<>(state, message, dto);
			return ResponseEntity.ok(apiResponse);

		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), null);
			return ResponseEntity.ok(apiResponse);
		}
		*/
	}

	// 後台：網頁內容管理介面
	@GetMapping("/news")
	public ResponseEntity<ApiResponse<List<NewsDtoForBack>>> findAllNews() {
		 return handleServiceCall(() -> {
	            List<NewsDtoForBack> news = newsService.findAllNewsForBack();
	            return new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
	        });
		 /*
		List<NewsDtoForBack> news = null;
		try {
			news = newsService.findAllNewsForBack();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), news);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), news);
			return ResponseEntity.ok(apiResponse);
		}
		*/

	}

	// 新增文章時的標籤選項
	@GetMapping("/tags")
	public ResponseEntity<ApiResponse<List<Tag>>> findAllTags() {
		return handleServiceCall(() -> {
            List<Tag> tags = newsService.findAllTags();
            return new ApiResponse<>(true, StatusMessage.查詢成功.name(), tags);
        });
		/*
		List<Tag> tags = null;
		try {
			tags = newsService.findAllTags();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), tags);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), tags);
			return ResponseEntity.ok(apiResponse);
		}
		*/
	}

	// 新增文章時的記者選項
	@GetMapping("/journalists")
	public ResponseEntity<ApiResponse<List<Journalist>>> findAllJournalists() {
		return handleServiceCall(() -> {
            List<Journalist> journalists = newsService.findAllJournalists();
            return new ApiResponse<>(true, StatusMessage.查詢成功.name(), journalists);
        });
		/*
		try {
			List<Journalist> journalists = newsService.findAllJournalists();
			ApiResponse apiResponse = new ApiResponse<>(true, StatusMessage.查詢成功.name(), journalists);
			return ResponseEntity.ok(apiResponse);
		} catch (Exception e) {
			ApiResponse apiResponse = new ApiResponse<>(false, e.toString(), null);
			return ResponseEntity.ok(apiResponse);
		}
		*/
	}

	// 新增文章
	@PostMapping("/news")
	public ResponseEntity<ApiResponse<Void>> postNews(@RequestBody News news) {
		 return handleServiceCall(() -> {
	            Boolean state = newsService.postNews(news);
	            String message = state ? StatusMessage.新增成功.name() : StatusMessage.新增失敗.name();
	            return new ApiResponse<>(state, message, null);
	        });
		 /*
		Boolean state = newsService.postNews(news);
		String message = state ? StatusMessage.新增成功.name() : StatusMessage.新增失敗.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
		*/

	}

	// 找到單篇文章（為了修改）
	@GetMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<News>> getNews(@PathVariable Integer newsId) {
		return handleServiceCall(() -> {
            News news = newsService.getNewsById(newsId);
            Boolean state = news != null;
            String message = state ? StatusMessage.查詢成功.name() : StatusMessage.查無資料.name();
            return new ApiResponse<>(state, message, news);
        });
		/*
		News news = newsService.getNewsById(newsId);
		Boolean state = news != null;
		String message = state ? StatusMessage.查詢成功.name() : StatusMessage.查無資料.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
		*/
	}

	// 修改文章
	@PutMapping("/news/{newsId}")
	public ResponseEntity<ApiResponse<Void>> updateNews(@PathVariable Integer newsId, @RequestBody News news) {
		return handleServiceCall(() -> {
            Boolean state = newsService.updateNews(newsId, news);
            String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
            return new ApiResponse<>(state, message, null);
        });
		/*
		Boolean state = newsService.updateNews(newsId, news);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse<News> apiResponse = new ApiResponse<>(state, message, news);
		return ResponseEntity.ok(apiResponse);
		*/
	}

	@PatchMapping("/publish/{newsId}")
	public ResponseEntity<ApiResponse<Void>> publish(@PathVariable Integer newsId,
			@RequestBody Map<String, Object> map) {
		// System.out.println(map);
		return handleServiceCall(() -> {
            Boolean isPublic = (Boolean) map.get("public");
            Boolean state = newsService.publishNews(newsId, isPublic);
            String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
            return new ApiResponse<>(state, message, null);
        });
		/*
		Boolean isPublic = (Boolean) map.get("public");
		Boolean state = newsService.publishNews(newsId, isPublic);
		String message = state ? StatusMessage.更新成功.name() : StatusMessage.更新失敗.name();
		ApiResponse<Map> apiResponse = new ApiResponse<>(state, message, map);
		return ResponseEntity.ok(apiResponse);
		*/
	}
	
	@GetMapping("/statistic/topsavednews")
	public ResponseEntity<ApiResponse<List<TopSavedNews>>> getTopSavedNews(){
		return handleServiceCall(()->{
			List<TopSavedNews> topSavedNews = functionService.getTopSavedNews();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), topSavedNews);
		});
	}
	
	@GetMapping("/statistic/topjournalists")
	public ResponseEntity<ApiResponse<List<TopJournalists>>> getTopJournalists(){
		return handleServiceCall(()->{
			List<TopJournalists> topJournalists = functionService.getTopJournalists();
			return new ApiResponse<>(true, StatusMessage.查詢成功.name(), topJournalists);
		});
	}
	

	private <T> ResponseEntity<ApiResponse<T>> handleServiceCall(ServiceCall<T> serviceCall) {
        try {
            ApiResponse<T> apiResponse = serviceCall.execute();
            return ResponseEntity.ok(apiResponse);
        } catch (Exception e) {
            ApiResponse<T> apiResponse = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
        }
    }
	
	@FunctionalInterface
	private interface ServiceCall<T> {
		ApiResponse<T> execute() throws Exception;
	}

}
