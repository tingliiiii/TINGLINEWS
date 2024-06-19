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

	// 前台 ============================================================
	// 確認文章已公開

	// 首頁
	public List<News> findAllNewsForFront() {
		// 先從緩存查找資料，沒有再連接資料庫
		List<News> newsList = (List<News>) redisTemplate.opsForValue().get(NEWS_CACHE_KEY);
		if (newsList == null) {
			newsList = newsDao.findAllNewsForFront();
			redisTemplate.opsForValue().set(NEWS_CACHE_KEY, newsList, 1, TimeUnit.HOURS);
		}
		return newsList;
	}

	// 標籤頁
	public List<News> findNewsByTagId(Integer tagId) {
		String cacheKey = TAG_CACHE_KEY_PREFIX + tagId;
		List<News> newsList = (List<News>) redisTemplate.opsForValue().get(cacheKey);
		if (newsList == null) {
			newsList = newsDao.findNewsByTagId(tagId);
			redisTemplate.opsForValue().set(cacheKey, newsList, 1, TimeUnit.HOURS);
		}
		return newsList;
	}

	// 單篇文章
	public NewsDtoForFront getNewsByIdForFront(Integer newsId) {
		String cacheKey = NEWS_ID_CACHE_KEY_PREFIX + newsId;
		NewsDtoForFront dto = (NewsDtoForFront) redisTemplate.opsForValue().get(cacheKey);
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
			redisTemplate.opsForValue().set(cacheKey, dto, 1, TimeUnit.HOURS);
		}
		return dto;
	}

	// 清理緩存
	private void clearAllNewsCache() {
		redisTemplate.delete(NEWS_CACHE_KEY);
	}

	private void clearNewsCacheById(Integer newsId) {
		String cacheKey = NEWS_ID_CACHE_KEY_PREFIX + newsId;
		redisTemplate.delete(cacheKey);
	}

	private void clearNewsCacheByTagId(Integer tagId) {
		String cacheKey = TAG_CACHE_KEY_PREFIX + tagId;
		redisTemplate.delete(cacheKey);
	}

}
