package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.SavedDto;
import com.example.demo.model.po.Saved;

@Repository
public class SavedDaoImpl implements SavedDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addSaved(Saved saved) {
		String sql = "INSERT INTO saved(user_id, news_id) VALUES(?, ?)";
		return jdbcTemplate.update(sql, saved.getUserId(), saved.getNewsId());
	}

	@Override
	public int deleteSaved(Integer savedId) {
		String sql = "DELETE FROM saved WHERE saved_id=?";
		return jdbcTemplate.update(sql, savedId);
	}

	@Override
	public List<Saved> findSavedById(Integer userId) {
		String sql = "SELECT saved_id, user_id, news_id, saved_time FROM saved WHERE user_id=?";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Saved.class), userId);
	}

}
