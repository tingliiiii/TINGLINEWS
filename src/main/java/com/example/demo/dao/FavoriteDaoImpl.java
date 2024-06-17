package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.po.Favorite;

@Repository
public class FavoriteDaoImpl implements FavoriteDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addFavorite(Favorite favorite) {
		String sql = "INSERT INTO saved(user_id, news_id) VALUES(?, ?)";
		return jdbcTemplate.update(sql, favorite.getUserId(), favorite.getNewsId());
	}

	@Override
	public int deleteFavorite(Integer favoriteId) {
		String sql = "DELETE FROM saved WHERE saved_id=?";
		return jdbcTemplate.update(sql, favoriteId);
	}

	@Override
	public List<Favorite> findFavoriteByUserId(Integer userId) {
		String sql = "SELECT saved_id, user_id, news_id, saved_time FROM saved "
				+ "WHERE user_id=? "
				+ "ORDER BY saved_time DESC ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Favorite.class), userId);
	}

	@Override
	public List<Favorite> findAllFavorites() {
		String sql = "SELECT saved_id, user_id, news_id, saved_time FROM saved";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Favorite.class));
	}

	@Override
	public List<TopNewsByFavorites> getTopNewsByFavorites() {
		String sql = "SELECT n.news_id, n.title, COUNT(DISTINCT s.user_id) AS count "
				+ "FROM news n "
				+ "JOIN saved s ON n.news_id = s.news_id "
				+ "GROUP BY n.news_id, n.title "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopNewsByFavorites.class));
	}

	@Override
	public List<TopJournalistsByFavorites> getTopJournalistsByFavorites() {
		String sql = "SELECT j.user_id AS journalist_id, u.user_name AS journalist_name, "
				+ "COUNT(DISTINCT s.news_id) AS count "
				+ "FROM saved s "
				+ "JOIN news_journalist j ON s.news_id = j.news_id "
				+ "JOIN user u ON j.user_id = u.user_id "
				+ "GROUP BY j.user_id, u.user_name "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopJournalistsByFavorites.class));
	}
	
	

}
