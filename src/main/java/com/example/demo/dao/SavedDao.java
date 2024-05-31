package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Saved;

public interface SavedDao {

	int addSaved(Saved saved);
	int deleteSaved(Integer savedId);
	List<Saved> findSavedById(Integer userId);
}
