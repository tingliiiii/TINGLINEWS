package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.TopJournalists;
import com.example.demo.model.dto.TopSavedNews;
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
		String sql = "SELECT saved_id, user_id, news_id, saved_time FROM saved "
				+ "WHERE user_id=? "
				+ "ORDER BY saved_time DESC ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Saved.class), userId);
	}

	@Override
	public List<Saved> findAllSaveds() {
		String sql = "SELECT saved_id, user_id, news_id, saved_time FROM saved";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Saved.class));
	}

	@Override
	public List<TopSavedNews> getTopSavedNews() {
		String sql = "SELECT n.news_id, n.title, COUNT(DISTINCT s.user_id) AS count "
				+ "FROM news n "
				+ "JOIN saved s ON n.news_id = s.news_id "
				+ "GROUP BY n.news_id, n.title "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopSavedNews.class));
	}

	@Override
	public List<TopJournalists> getTopJournalists() {
		String sql = "SELECT j.user_id AS journalist_id, u.user_name AS journalist_name, "
				+ "COUNT(DISTINCT s.news_id) AS count "
				+ "FROM saved s "
				+ "JOIN news_journalist j ON s.news_id = j.news_id "
				+ "JOIN user u ON j.user_id = u.user_id "
				+ "GROUP BY j.user_id, u.user_name "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopJournalists.class));
	}
	
	

}
