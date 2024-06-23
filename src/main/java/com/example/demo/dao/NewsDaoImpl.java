package com.example.demo.dao;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.po.Journalist;
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
		String sql = "INSERT INTO news(title, content, tag_id, user_id, image) VALUES(:title, :content, :tagId, :userId, :image)";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(news);

		// keyHolder, new String[] {"news_id"} 將主鍵欄位 news_id 所自動生成的序號放到 keyHolder 中
		KeyHolder keyHolder = new GeneratedKeyHolder();
		namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "news_id" });
		int newsId = keyHolder.getKey().intValue();

		for (Integer userId : news.getJournalistIds()) {
			addJournalist(newsId, userId);
		}
		return newsId;
	}

	@Override
	public int publishNews(Integer newsId, Boolean isPublic) {
		String sql = "UPDATE news SET public=?, public_time=?, updated_time=? WHERE news_id=?";
		return jdbcTemplate.update(sql, isPublic, new Date(), new Date(), newsId);
	}

	@Override
	public int updateNews(Integer newsId, News news) {
		String sql = "UPDATE news SET title=?, content=?, tag_id=?, updated_time=?, image=? WHERE news_id=?";
		int rowcount = jdbcTemplate.update(sql, news.getTitle(), news.getContent(), news.getTagId(), new Date(),
				news.getImage(), newsId);
		deleteJournalistByNewsId(newsId);
		for (Integer journalistId : news.getJournalistIds()) {
			addJournalist(newsId, journalistId);
		}
		return rowcount;
	}

	@Override
	public int deleteNews(Integer newsId) {
		String sql = "DELETE FROM news WHERE news_id=?";
		return jdbcTemplate.update(sql, newsId);
	}

	@Override
	public News getNewsById(Integer newsId) {
		String sql = "SELECT news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time, image FROM news WHERE news_id=?";
		try {
			News news = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(News.class), newsId);
			Integer[] journalistIds = queryJournalistsByNewsId(newsId);
			news.setJournalistIds(journalistIds);
			return news;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public News getNewsByIdForFront(Integer newsId) {
		String sql = "SELECT news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time, image "
				+ "FROM news WHERE public=1 && news_id=? ";
		try {
			News news = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(News.class), newsId);
			Integer[] journalistIds = queryJournalistsByNewsId(newsId);
			news.setJournalistIds(journalistIds);
			return news;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<News> findNewsByTagIdAndNewsId(Integer tagId, Integer newsId) {
		String sql = "SELECT news_id, title, content, tag_id, user_id, public, public_time, image "
				+ "FROM news WHERE public=1 && tag_id=? && news_id<? ORDER BY public_time DESC LIMIT 3";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(News.class), tagId, newsId);
	}

	@Override
	public List<News> findNewsByTagId(Integer tagId) {
		String sql = "SELECT news_id, title, content, tag_id, user_id, public, public_time, image "
				+ "FROM news WHERE public=1 && tag_id=? " + "ORDER BY public_time DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper(News.class), tagId);
	}

	@Override
	public List<News> findAllNewsForFront() {
		String sql = "SELECT news_id, title, content, tag_id, user_id, public, public_time, image "
				+ "FROM news WHERE public=1 " + "ORDER BY public_time DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(News.class));
	}

	@Override
	public List<News> findAllNewsForBack() {
		String sql = "SELECT news_id, title, content, tag_id, user_id, created_time, updated_time, public, public_time FROM news";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(News.class));
	}

	@Override
	public List<Tag> findAllTags() {
		String sql = "SELECT tag_id, tag_name FROM tags";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Tag.class));
	}

	@Override
	public Tag getTagById(Integer tagId) {
		String sql = "SELECT tag_id, tag_name FROM tags WHERE tag_id=?";
		try {
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Tag.class), tagId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Journalist> findAllJournalists() {
		String sql = "SELECT user_id, user_name FROM users WHERE authority_id>=2 and authority_id<5 ORDER BY user_id DESC";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Journalist.class));
	}

	@Override
	public Journalist getJournalistById(Integer userId) {
		String sql = "SELECT user_id, user_name FROM users WHERE user_id=?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Journalist.class), userId);
	}

	@Override
	public int addJournalist(Integer newsId, Integer userId) {
		String sql = "INSERT INTO news_journalist(news_id, user_id) VALUES (?, ?)";
		return jdbcTemplate.update(sql, newsId, userId);
	}

	@Override
	public int deleteJournalistByNewsId(Integer newsId) {
		String sql = "DELETE FROM news_journalist WHERE news_id = ?";
		return jdbcTemplate.update(sql, newsId);
	}

	private Integer[] queryJournalistsByNewsId(Integer newsId) {
		String sql = "SELECT user_id FROM news_journalist WHERE news_id=?";
		List<Map<String, Object>> journalistList = jdbcTemplate.queryForList(sql, newsId);
		Integer[] journalistIds = journalistList.stream().map(data -> (Integer) data.get("user_id"))
				.toArray(Integer[]::new);
		return journalistIds;
	}

}
