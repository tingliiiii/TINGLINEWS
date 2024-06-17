package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Donation;

public interface DonationDao {
	
	int addDonation(Donation donation);
	int stopDonation(Integer donationId);
	List<Donation> findDonationsByUserId(Integer userId);
	List<Donation> findAllDonations();
}
