package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DonationDao;
import com.example.demo.dao.FavoriteDao;
import com.example.demo.dao.NewsDao;
import com.example.demo.model.dto.FavoriteDto;
import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.dto.TopTagsByFavorites;
import com.example.demo.model.po.Donation;
import com.example.demo.model.po.News;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import com.example.demo.model.po.Favorite;

@Slf4j
@Service
public class FunctionService {

	@Autowired
	private DonationDao donatedDao;

	@Autowired
	private FavoriteDao favoriteDao;

	@Autowired
	private NewsDao newsDao;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private static final String USER_PROFILE_CACHE_KEY_PREFIX = "userProfile:";

	public boolean addDonation(Donation donation) {
		Boolean result = donatedDao.addDonation(donation) > 0;
		if (result) {
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + donation.getUserId());
		}
		return result;
	}

	public boolean stopDonation(Integer donationId) {
		Integer userId = donatedDao.getDonation(donationId).getUserId();
		Boolean result = donatedDao.stopDonation(donationId) > 0;
		if (result) {
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + userId);
		}
		return result;
	}

	public List<Donation> findDonationsByUserId(Integer userId) {
		return donatedDao.findDonationsByUserId(userId);
	}

	public boolean addFavorite(Favorite favorite) {
		Boolean result = favoriteDao.addFavorite(favorite) > 0;
		if (result) {
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + favorite.getUserId());
		}
		return result;
	}

	public boolean deleteFavorite(Integer favoriteId) {
		Integer userId = favoriteDao.getFavorite(favoriteId).getUserId();
		Boolean result = favoriteDao.deleteFavorite(favoriteId) > 0;
		if (result) {
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + userId);
		}
		return result;
	}

	// 收藏紀錄
	public List<FavoriteDto> findFavoriteByUserId(Integer userId) {

		List<Favorite> favorites = favoriteDao.findFavoriteByUserId(userId);
		List<FavoriteDto> favoriteDtos = new ArrayList<>();
		for (Favorite favorite : favorites) {
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
	public List<TopNewsByFavorites> getTopNewsByFavorites() {
		return favoriteDao.getTopNewsByFavorites();
	}

	public List<TopJournalistsByFavorites> getTopJournalistsByFavorites() {
		return favoriteDao.getTopJournalistsByFavorites();
	}

	public List<TopTagsByFavorites> getTopTagsByFavorites() {
		return favoriteDao.getTopTagsByFavorites();
	}

	// Redis ============================================================

	private void clearCache(String cacheKey) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
			redisTemplate.delete(cacheKey);
			log.debug("Cleared cache for key: " + cacheKey);
		}
	}

}
