package com.example.demo.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
	private NewsDao newsDao;

	@Autowired
	private UserService userService;

	@Autowired
	private ModelMapper modelMapper;

	// 後台 ============================================================

	// 上稿
	public boolean postNews(News news) {
		return newsDao.postNews(news) > 0;
	}

	// 上稿時的標籤選項
	public List<Tag> findAllTags() {
		return newsDao.findAllTags();
	}
	
	public List<Journalist> findAllJournalists(){
		return newsDao.findAllJournalists();
	}

	// 發布或下架新聞
	public boolean publishNews(Integer newsId, Boolean isPublic) {
		return newsDao.publishNews(newsId, isPublic) > 0;
	}

	// 修改前要先找到該文章
	public News getNewsById(Integer newsId) {
		return newsDao.getNewsById(newsId);
	}

	// 修改新聞
	public boolean updateNews(Integer newsId, News news) {
		return newsDao.updateNews(newsId, news) > 0;
	}

	// 刪除新聞
	public int deleteNews(Integer newsId) {
		return newsDao.deleteNews(newsId);
	}

	// 網頁內容管理
	public List<NewsDtoForBack> findAllNewsForBack() {
		List<News> newsList = newsDao.findAllNewsForBack();
		List<NewsDtoForBack> dtos = newsList.stream().map(news -> {
			NewsDtoForBack dto = modelMapper.map(news, NewsDtoForBack.class);
			dto.setUserName(userService.getUserById(news.getUserId()).getUserName());
			return dto;
		}).collect(Collectors.toList());
		/*
		for (News news : newsList) {
			NewsDtoForBack dto = modelMapper.map(news, NewsDtoForBack.class);
			dto.setUserName(userService.getUserById(news.getUserId()).getUserName());
			dtos.add(dto);
		}
		*/
		return dtos;
	}


	// 前台 ============================================================
	// 確認文章已公開

	// 首頁
	public List<News> findAllNewsForFront() {
		return newsDao.findAllNewsForFront();
	}

	// 標籤頁
	public List<News> findNewsByTagId(Integer tagId) {
		return newsDao.findNewsByTagId(tagId);
	}

	// 單篇文章
	public NewsDtoForFront getNewsByIdForFront(Integer newsId) {
		News news = newsDao.getNewsByIdForFront(newsId);
		if (news == null) {
            return null;
        }
		NewsDtoForFront dto = modelMapper.map(news, NewsDtoForFront.class);
		dto.setTag(newsDao.getTagById(news.getTagId()));
		List<Journalist> journalists = new ArrayList<>();
		for (Integer journalistId : news.getJournalistIds()) {
			journalists.add(newsDao.getJournalistById(journalistId));
		}
		dto.setJournalists(journalists);
		return dto;
	}

	
}
