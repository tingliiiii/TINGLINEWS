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

import com.example.demo.model.po.Donated;

@Repository
public class DonatedDaoImpl implements DonatedDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public int addDonated(Donated donated) {
		String sql = "INSERT INTO donated(frequency, amount, end_time, donate_status, user_id) "
				+ "VALUES(:frequency, :amount, :endTime, :donateStatus, :userId)";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(donated);
		return namedParameterJdbcTemplate.update(sql, params);

	}

	@Override
	public int stopDanted(Integer donatedId) {
		String sql = "UPDATE donated SET donate_status=?, end_time=? WHERE donated_id=?";
		return jdbcTemplate.update(sql, "已完成", new Date(), donatedId);
	}

	@Override
	public List<Donated> findDonatedById(Integer userId){
		String sql = "SELECT donated_id, frequency, amount, donated_time, end_time, donate_status, user_id FROM donated WHERE user_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Donated.class), userId);
	}

	@Override
	public List<Donated> findAllDonateds() {
		String sql = "SELECT donated_id, frequency, amount, donated_time, end_time, donate_status, user_id FROM donated";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Donated.class));
	}

}
