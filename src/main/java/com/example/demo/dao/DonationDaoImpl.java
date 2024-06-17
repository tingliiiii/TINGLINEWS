package com.example.demo.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.po.Donation;

@Repository
public class DonationDaoImpl implements DonationDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public int addDonation(Donation donation) {
		String sql = "INSERT INTO donations(frequency, amount, end_time, donate_status, user_id) "
				+ "VALUES(:frequency, :amount, :endTime, :donateStatus, :userId)";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(donation);
		return namedParameterJdbcTemplate.update(sql, params);
	}

	@Override
	public int stopDonation(Integer donationId) {
		String sql = "UPDATE donations SET donate_status=?, end_time=? WHERE donation_id=?";
		return jdbcTemplate.update(sql, "已完成", new Date(), donationId);
	}

	@Override
	public List<Donation> findDonationsByUserId(Integer userId){
		String sql = "SELECT donation_id, frequency, amount, donated_time, end_time, donate_status, user_id FROM donations WHERE user_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Donation.class), userId);
	}

	@Override
	public List<Donation> findAllDonations() {
		String sql = "SELECT donation_id, frequency, amount, donated_time, end_time, donate_status, user_id FROM donations";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Donation.class));
	}

}
