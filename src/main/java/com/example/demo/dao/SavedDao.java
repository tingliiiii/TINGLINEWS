package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.dto.TopJournalists;
import com.example.demo.model.dto.TopSavedNews;
import com.example.demo.model.po.Saved;

public interface SavedDao {

	int addSaved(Saved saved);
	int deleteSaved(Integer savedId);
	List<Saved> findSavedById(Integer userId);
	List<Saved> findAllSaveds();
	// 統計
	List<TopSavedNews> getTopSavedNews();
	List<TopJournalists> getTopJournalists();
	
}
