package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Donated;

public interface DonatedDao {
	
	int addDonated(Donated donated);
	int stopDanted(Integer donatedId);
	List<Donated> findDonatedById(Integer userId);
	List<Donated> findAllDonateds();
}
