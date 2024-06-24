package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.dto.TopTagsByFavorites;
import com.example.demo.model.po.Favorite;

public interface FavoriteDao {

	int addFavorite(Favorite favorite);
	int deleteFavorite(Integer favoriteId);
	Favorite getFavorite(Integer favoriteId);
	List<Favorite> findFavoriteByUserId(Integer userId);
	List<Favorite> findAllFavorites();
	// 統計
	List<TopNewsByFavorites> getTopNewsByFavorites();
	List<TopJournalistsByFavorites> getTopJournalistsByFavorites();
	List<TopTagsByFavorites> getTopTagsByFavorites();
}
