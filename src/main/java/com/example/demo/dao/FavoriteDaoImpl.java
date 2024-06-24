package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.dto.TopJournalistsByFavorites;
import com.example.demo.model.dto.TopNewsByFavorites;
import com.example.demo.model.dto.TopTagsByFavorites;
import com.example.demo.model.po.Favorite;

@Repository
public class FavoriteDaoImpl implements FavoriteDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addFavorite(Favorite favorite) {
		String sql = "INSERT INTO favorites(user_id, news_id) VALUES(?, ?)";
		return jdbcTemplate.update(sql, favorite.getUserId(), favorite.getNewsId());
	}

	@Override
	public int deleteFavorite(Integer favoriteId) {
		String sql = "DELETE FROM favorites WHERE favorite_id=?";
		return jdbcTemplate.update(sql, favoriteId);
	}
	
	@Override
	public Favorite getFavorite(Integer favoriteId) {
		String sql = "SELECT favorite_id, user_id, news_id, favorite_time FROM favorites WHERE favorite_id=? ";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Favorite.class), favoriteId);
	}

	@Override
	public List<Favorite> findFavoriteByUserId(Integer userId) {
		String sql = "SELECT favorite_id, user_id, news_id, favorite_time FROM favorites "
				+ "WHERE user_id=? "
				+ "ORDER BY favorite_time DESC ";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Favorite.class), userId);
	}

	@Override
	public List<Favorite> findAllFavorites() {
		String sql = "SELECT favorite_id, user_id, news_id, favorite_time FROM favorites";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Favorite.class));
	}

	@Override
	public List<TopNewsByFavorites> getTopNewsByFavorites() {
		String sql = "SELECT n.news_id, n.title, COUNT(DISTINCT f.user_id) AS count "
				+ "FROM news n "
				+ "JOIN favorites f ON n.news_id = f.news_id "
				+ "GROUP BY n.news_id, n.title "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopNewsByFavorites.class));
	}

	@Override
	public List<TopJournalistsByFavorites> getTopJournalistsByFavorites() {
		String sql = "SELECT j.user_id AS journalist_id, u.user_name AS journalist_name, "
				+ "COUNT(DISTINCT f.news_id) AS count "
				+ "FROM favorites f "
				+ "JOIN news_journalist j ON f.news_id = j.news_id "
				+ "JOIN users u ON j.user_id = u.user_id "
				+ "GROUP BY j.user_id, u.user_name "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopJournalistsByFavorites.class));
	}

	@Override
	public List<TopTagsByFavorites> getTopTagsByFavorites() {
		String sql = "SELECT t.tag_name AS tag, COUNT(DISTINCT f.news_id) AS count "
				+ "FROM favorites f "
				+ "JOIN news n ON f.news_id = n.news_id "
				+ "JOIN tags t ON n.tag_id = t.tag_id "
				+ "GROUP BY t.tag_name "
				+ "ORDER BY count DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(TopTagsByFavorites.class));
	}
	
	

}
