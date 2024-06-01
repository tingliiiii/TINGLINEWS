package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.DonatedDao;
import com.example.demo.dao.NewsDao;
import com.example.demo.dao.SavedDao;
import com.example.demo.model.dto.SavedDto;
import com.example.demo.model.po.Donated;
import com.example.demo.model.po.News;
import com.example.demo.model.po.Saved;

@Service
public class FunctionService {

	@Autowired
	private DonatedDao donatedDao;
	
	@Autowired
	private SavedDao savedDao;
	
	@Autowired
	private NewsDao newsDao;

	public int addDonated(Donated donated) {
		return donatedDao.addDonated(donated);
	}

	public int stopDanted(Integer donatedId) {
		return donatedDao.stopDanted(donatedId);
	}

	public List<Donated> findDonatedById(Integer userId) {
		return donatedDao.findDonatedById(userId);
	}
	
	public int addSaved(Saved saved) {
		return savedDao.addSaved(saved);
	}

	public int deleteSaved(Integer savedId) {
		return savedDao.deleteSaved(savedId);
	}

	// 收藏紀錄
	public List<SavedDto> findSavedById(Integer userId) {
		
		List<Saved> saveds = savedDao.findSavedById(userId);
		List<SavedDto> savedDtos = new ArrayList<>();
		for(Saved saved : saveds) {
			SavedDto savedDto = new SavedDto();
			savedDto.setSavedId(saved.getSavedId());
			savedDto.setUserId(userId);
			savedDto.setSavedTime(saved.getSavedTime());
			News news = newsDao.getNewsById(saved.getNewsId());
			savedDto.setNews(news);
			savedDtos.add(savedDto);
		}
		
		return savedDtos;
	}
	
	
	
}
