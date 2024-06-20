package com.example.demo.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dao.NewsDao;
import com.example.demo.model.dto.NewsDtoForBack;
import com.example.demo.model.dto.NewsDtoForFront;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.po.Journalist;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;
import com.example.demo.model.response.GenericTypeReference;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class NewsService {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private NewsDao newsDao;

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	private static final String NEWS_CACHE_KEY = "allNews";
	private static final String TAG_CACHE_KEY_PREFIX = "newsByTag:";
	private static final String NEWS_ID_CACHE_KEY_PREFIX = "newsById:";

	private final ObjectMapper objectMapper = new ObjectMapper();

	// 後台 ============================================================

	// 上稿
	public boolean postNews(News news) {
		return newsDao.postNews(news) > 0;
	}

	// 上稿時的標籤選項
	public List<Tag> findAllTags() {
		return newsDao.findAllTags();
	}

	public List<Journalist> findAllJournalists() {
		return newsDao.findAllJournalists();
	}

	// 發布或下架新聞
	public boolean publishNews(Integer newsId, Boolean isPublic) {
		boolean result = newsDao.publishNews(newsId, isPublic) > 0;
		if (result) {
			// 如果發布新聞就清理緩存
			clearAllNewsCache();
			clearNewsCacheById(newsId);
			News news = newsDao.getNewsById(newsId);
			clearNewsCacheByTagId(news.getTagId());
		}
		return result;
	}

	// 修改前要先找到該文章
	public News getNewsById(Integer newsId) {
		return newsDao.getNewsById(newsId);
	}

	// 修改新聞
	public boolean updateNews(Integer newsId, News news) {
		boolean result = newsDao.updateNews(newsId, news) > 0;
		if (result) {
			// 如果修改新聞就清理緩存
			clearAllNewsCache();
			clearNewsCacheById(newsId);
			clearNewsCacheByTagId(news.getTagId());
		}
		return result;
	}

	// 刪除新聞
	public int deleteNews(Integer newsId) {
		int result = newsDao.deleteNews(newsId);
		if (result > 0) {
			// 如果刪除新聞就清理緩存
			clearAllNewsCache();
			clearNewsCacheById(newsId);
			News news = newsDao.getNewsById(newsId);
			clearNewsCacheByTagId(news.getTagId());
		}
		return result;
	}

	// 網頁內容管理
	public List<NewsDtoForBack> findAllNewsForBack() {
		List<News> newsList = newsDao.findAllNewsForBack();
		List<NewsDtoForBack> dtos = newsList.stream().map(news -> {
			NewsDtoForBack dto = modelMapper.map(news, NewsDtoForBack.class);
			dto.setUserName(userService.getUserById(news.getUserId()).getUserName());
			return dto;
		}).collect(Collectors.toList());
		return dtos;
	}

	// 前台（確認文章已公開）=================================================

	// 首頁
	public List<News> findAllNewsForFront() {
		// 先從緩存查找資料，沒有再連接資料庫
		List<News> newsList = getRedisList(NEWS_CACHE_KEY, News.class);
		if (newsList == null) {
			newsList = newsDao.findAllNewsForFront();
			setRedisList(NEWS_CACHE_KEY, newsList);
		}
		return newsList;
	}

	// 標籤頁
	public List<News> findNewsByTagId(Integer tagId) {
		String cacheKey = TAG_CACHE_KEY_PREFIX + tagId;
		// List<News> newsList = (List<News>) redisTemplate.opsForValue().get(cacheKey);
		List<News> newsList = getRedisList(cacheKey, News.class);
		if (newsList == null) {
			newsList = newsDao.findNewsByTagId(tagId);
			// redisTemplate.opsForValue().set(cacheKey, newsList, 1, TimeUnit.HOURS);
			setRedisList(cacheKey, newsList);
		}
		return newsList;
	}

	// 單篇文章
	public NewsDtoForFront getNewsByIdForFront(Integer newsId) {
		String cacheKey = NEWS_ID_CACHE_KEY_PREFIX + newsId;
		// NewsDtoForFront dto = (NewsDtoForFront)
		// redisTemplate.opsForValue().get(cacheKey);
		NewsDtoForFront dto = getRedisJson(cacheKey);
		if (dto == null) {
			News news = newsDao.getNewsByIdForFront(newsId);
			if (news == null) {
				return null;
			}
			dto = modelMapper.map(news, NewsDtoForFront.class);
			dto.setTag(newsDao.getTagById(news.getTagId()));
			List<Journalist> journalists = new ArrayList<>();
			for (Integer journalistId : news.getJournalistIds()) {
				journalists.add(newsDao.getJournalistById(journalistId));
			}
			dto.setJournalists(journalists);
			// redisTemplate.opsForValue().set(cacheKey, dto, 1, TimeUnit.HOURS);
			setRedisJson(cacheKey, dto);
		}
		return dto;
	}

	// Redis ============================================================

	private void clearCache(String cacheKey) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
			redisTemplate.delete(cacheKey);
			log.info("Cleared cache for key: " + cacheKey);
		}
	}

	private void clearAllNewsCache() {
		clearCache(NEWS_CACHE_KEY);
	}

	private void clearNewsCacheById(Integer newsId) {
		clearCache(NEWS_ID_CACHE_KEY_PREFIX + newsId);
	}

	private void clearNewsCacheByTagId(Integer tagId) {
		clearCache(TAG_CACHE_KEY_PREFIX + tagId);
	}

	private <T> void setRedisList(String key, List<T> list) {
		try {
			String value = objectMapper.writeValueAsString(list);
			redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Failed to set Redis list for key: " + key, e);
		}
	}

	private void setRedisJson(String key, Object object) {
		try {
			String value = objectMapper.writeValueAsString(object);
			redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Failed to set Redis JSON for key: " + key, e);
		}
	}

	private <T> List<T> getRedisList(String key, Class<T> elementType) {
		String json = (String) redisTemplate.opsForValue().get(key);
		if (json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, new GenericTypeReference<>(elementType));
		} catch (Exception e) {
			log.error("Failed to process JSON for key: " + key, e);
			return null;
		}
	}

	private <T> NewsDtoForFront getRedisJson(String key) {
		String json = (String) redisTemplate.opsForValue().get(key);
		if (json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, NewsDtoForFront.class);
		} catch (Exception e) {
			log.error("Failed to process JSON for key: " + key, e);
			return null;
		}
	}

}
