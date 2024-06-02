package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.NewsDao;
import com.example.demo.model.dto.NewsDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;

@Service
public class NewsService {

	@Autowired
	private NewsDao newsDao;
	
	@Autowired
	private UserService userService;

	// 後台 ============================================================
	
	// 上稿（員工）
	public boolean postNews(News news) {
		return newsDao.postNews(news)>0;
	}

	// 發布或下架新聞（編輯或管理員）
	public int publishNews(Integer newsId, News news) {
		return newsDao.publishNews(newsId, news);
	}

	// 修改前要先找到該文章
	public News getNewsById(Integer newsId) {
		return newsDao.getNewsById(newsId);
	}
	
	// 修改新聞（員工）
	// TODO 只能修改自己寫的新聞？發布後無法修改新聞？
	public boolean updateNews(Integer newsId, News news) {
		return newsDao.updateNews(newsId, news)>0;
	}

	// 刪除新聞（編輯或管理員）
	public int deleteNews(Integer newsId) {
		return newsDao.deleteNews(newsId);
	}

	// 網頁內容管理
	public List<NewsDto> findAllNewsForBack() {
		List<NewsDto> dtos = new ArrayList<>();
		List<News> newsList = newsDao.findAllNewsForBack();
		for(News news: newsList) {
			// System.out.println(news);
			NewsDto dto = new NewsDto();
			dto.setNewsId(news.getNewsId());
			dto.setTitle(news.getTitle());
			int id = news.getUserId();
			dto.setUserId(id);
			dto.setUserName(userService.getUserById(id).getUserName());
			dto.setCreatedTime(news.getCreatedTime());
			dto.setUpdatedTime(news.getUpdatedTime());
			dto.setPublic(news.isPublic());
			dto.setPublicTime(news.getPublicTime());
			dtos.add(dto);
		}
		return dtos; 
	}

	// 上稿時的標籤選項
	public List<Tag> findAllTags() {
		return newsDao.findAllTags();
	}
	
	// 前台 ============================================================
	// 確認文章已公開
	
	public List<News> findAllNewsForFront(){
		return newsDao.findAllNewsForFront();
	}
	
	public List<News> findNewsByTagId(Integer tagId){
		return newsDao.findNewsByTagId(tagId);
	}
	
	public News getNewsByIdForFront(Integer newsId) {
		return newsDao.getNewsByIdForFront(newsId);
	}
}
