package com.example.demo.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.example.demo.model.po.User;

@Repository
public class UserDaoImpl implements UserDao {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	// 註冊
	@Override
	public int addUser(User user) {
		String sql = "INSERT INTO user(userName, userEmail, userPassword, birthday, gender, phone) "
				+ "values(:userName, :userEmail, :userPassword, :birthday, :gender, :phone)";
		// 自動將 user 物件的屬性值給 SQL 參數(?)使用
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
		// KeyHolder
		KeyHolder keyHolder = new GeneratedKeyHolder();

		// keyHolder, new String[] {"id"} 將主鍵欄位 id 所自動生成的序號放到 keyHolder 中
		namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });

		int userId = keyHolder.getKey().intValue(); // 最新新增紀錄的 user id
		return userId;
	}

	// 更新使用者
	@Override
	public int updateUser(Integer userId, User user) {
		String sql = "UPDATE user SET userName=:userName, userEmail=:userEmail, userPassword=:userPassword, "
				+ "birthday=:birthday, gender=:gender, phone=:phone WHERE userId=:userId";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
		int rowcount = namedParameterJdbcTemplate.update(sql, params);
		return rowcount;
	}

	// 更新權限（管理員）
	@Override
	public int updateUserAuthority(Integer userId, Integer authorityId) {
		String sql = "UPDATE user SET authorityId=? WHERE userId=?";
		int rowcount = jdbcTemplate.update(sql, authorityId, userId);
		return rowcount;
	}
	
	// 刪除使用者（管理員）
	@Override
	public int deleteUser(Integer userId) {
		String sql = "DELETE FROM user WHERE userId=?";
		return jdbcTemplate.update(sql, userId);
	}

	// profile、後台修改刪除
	@Override
	public User getUserById(Integer userId) {
		String sql = "SELECT userId, userName, userEmail, birthday, gender, phone, donateId, authorityId FROM user WHERE userId=?";
		try {
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 後台：使用者管理頁面
	@Override
	public List<User> findAllUsers() {
		String sql = "SELECT userId, userName, userEmail, authorityId, registeredDate FROM user";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
	}
	
}
