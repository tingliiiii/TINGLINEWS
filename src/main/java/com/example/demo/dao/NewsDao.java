package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;

public interface NewsDao {

	int postNews(News news);
	int publishNews(Integer newsId);
	int updateNews(Integer newsId, News news);
	int deleteNews(Integer newsId);
	News getNewsById(Integer newsId);
	List<News> findAllNews();
	List<Tag> findAllTags();
	
}
