package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DonationDao;
import com.example.demo.dao.FavoriteDao;
import com.example.demo.dao.NewsDao;
import com.example.demo.model.dto.FavoriteDto;
import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.po.Donation;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Favorite;

@Service
public class FunctionService {

	@Autowired
	private DonationDao donatedDao;
	
	@Autowired
	private FavoriteDao favoriteDao;
	
	@Autowired
	private NewsDao newsDao;

	public boolean addDonation(Donation donated) {
		return donatedDao.addDonation(donated)>0;
	}

	public boolean stopDonation(Integer donatedId) {
		return donatedDao.stopDonation(donatedId)>0;
	}

	public List<Donation> findDonationsByUserId(Integer userId) {
		return donatedDao.findDonationsByUserId(userId);
	}
	
	public boolean addSaved(Favorite saved) {
		return favoriteDao.addFavorite(saved)>0;
	}

	public boolean deleteSaved(Integer savedId) {
		return favoriteDao.deleteFavorite(savedId)>0;
	}

	// 收藏紀錄
	public List<FavoriteDto> findFavoriteByUserId(Integer userId) {
		
		List<Favorite> favorites = favoriteDao.findFavoriteByUserId(userId);
		List<FavoriteDto> favoriteDtos = new ArrayList<>();
		for(Favorite favorite : favorites) {
			FavoriteDto favoriteDto = new FavoriteDto();
			favoriteDto.setFavoriteId(favorite.getFavoriteId());
			favoriteDto.setUserId(userId);
			favoriteDto.setFavoriteTime(favorite.getFavoriteTime());
			News news = newsDao.getNewsById(favorite.getNewsId());
			favoriteDto.setNews(news);
			favoriteDtos.add(favoriteDto);
		}
		
		return favoriteDtos;
	}
	
	// 新聞收藏數統計
	public List<TopNewsByFavorites> getTopNewsByFavorites(){
		return favoriteDao.getTopNewsByFavorites();
	}
	
	public List<TopJournalistsByFavorites> getTopJournalistsByFavorites(){
		return favoriteDao.getTopJournalistsByFavorites();
	}
	
	
}
