package com.example.demo.dao;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.po.News;
import com.example.demo.model.po.Tag;

@Repository
public class NewsDaoImpl implements NewsDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Override
	public int postNews(News news) {
		String sql = "INSERT INTO news(title, content, tag_id, user_id) VALUES(:title, :content, :tagId, :userId)";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(news);
		return namedParameterJdbcTemplate.update(sql, params);
	}
	
	@Override
	public int publishNews(Integer newsId, News news) {
		String sql = "UPDATE news SET public=?, public_time=? WHERE news_id=?";
		return jdbcTemplate.update(sql, news.isPublic(), new Date(), newsId);
	}
	
	@Override
	public int updateNews(Integer newsId, News news) {
		String sql = "UPDATE news SET title=?, content=?, tag_id=?, updated_time=? WHERE news_id=?";
		return jdbcTemplate.update(sql, news.getTitle(), news.getContent(), news.getTagId(), new Date(), newsId);
	}
	
	// news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time

	@Override
	public int deleteNews(Integer newsId) {
		String sql = "DELETE FROM news WHERE news_id=?";
		return jdbcTemplate.update(sql, newsId);
	}

	@Override
	public News getNewsById(Integer newsId) {
		String sql = "SELECT news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time FROM news WHERE news_id=?";
		try {
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(News.class), newsId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<News> findAllNews() {
		String sql = "SELECT news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time FROM news";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(News.class));
	}

	@Override
	public List<Tag> findAllTags() {
		String sql = "SELECT tag_id, tag_name FROM tag";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tag.class));
	}

}
