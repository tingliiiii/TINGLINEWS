package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Journalist;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;

public interface NewsDao {

	// 後台 ============================================================
	
	// 新增
	int postNews(News news);
	List<Tag> findAllTags();
	// 修改
	News getNewsById(Integer newsId);
	int updateNews(Integer newsId, News news);
	// 刪除
	int deleteNews(Integer newsId);
	// 公開
	int publishNews(Integer newsId, Boolean isPublic);
	// 網站內容管理
	List<News> findAllNewsForBack();

	// 前台 ============================================================

	// 單篇報導
	News getNewsByIdForFront(Integer newsId); // 只有已公開
	Tag getTagById(Integer tagId);
	List<News> findNewsByTagIdAndNewsId(Integer tagId, Integer newsId);
	// 根據標籤的報導列表
	List<News> findNewsByTagId(Integer tagId);
	// 報導列表（首頁、即時）
	List<News> findAllNewsForFront();
	
	// 該篇報導的記者名單
	List<Journalist> findAllJournalists();
	Journalist getJournalistById(Integer userId);
	int addJournalist(Integer newsId, Integer userId);
	int deleteJournalistByNewsId(Integer newsId);

}
