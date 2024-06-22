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

import com.example.demo.model.po.Authority;
import com.example.demo.model.po.Donation;
import com.example.demo.model.po.Favorite;
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
		String sql = "INSERT INTO users(user_name, user_email, user_password, salt, birthday, gender, phone) "
				+ "values(:userName, :userEmail, :userPassword, :salt, :birthday, :gender, :phone)";
		// 自動將 user 物件的屬性值給 SQL 參數(?)使用
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
		// KeyHolder
		KeyHolder keyHolder = new GeneratedKeyHolder();

		// keyHolder, new String[] {"id"} 將主鍵欄位 id 所自動生成的序號放到 keyHolder 中
		namedParameterJdbcTemplate.update(sql, params, keyHolder, new String[] { "id" });

		int userId = keyHolder.getKey().intValue();
		return userId;
	}

	// 更新使用者
	@Override
	public int updateUser(Integer userId, User user) {
		String sql = "UPDATE users SET user_name=:userName, user_email=:userEmail, "
				+ "birthday=:birthday, gender=:gender, phone=:phone WHERE user_id=:userId";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
		params.registerSqlType("userId", java.sql.Types.INTEGER); // 確保userId被正確綁定
		int rowcount = namedParameterJdbcTemplate.update(sql, params);
		return rowcount;
	}

	// 刪除使用者（管理員）
	@Override
	public int deleteUser(Integer userId) {
		String sql = "DELETE FROM users WHERE user_id=?";
		return jdbcTemplate.update(sql, userId);
	}

	// profile、後台修改刪除
	@Override
	public User getUserById(Integer userId) {
		String sql = "SELECT user_id, user_name, user_email, birthday, gender, phone, authority_id, registered_time FROM users WHERE user_id=?";
		try {
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userId);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 登入：用帳號找使用者
	@Override
	public User getUserByEmail(String userEmail) {
		String sql = "SELECT user_id, user_name, user_email, user_password, salt FROM users WHERE user_email = ?";
		try {
			User user = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userEmail);
			return user;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	// 後台：使用者管理頁面
	@Override
	public List<User> findAllUsers() {
		String sql = "SELECT user_id, user_name, user_email, authority_id, registered_time FROM users";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
	}

	// 更新權限（管理員）
	@Override
	public int updateUserAuthority(Integer userId, Integer authorityId) {
		String sql = "UPDATE users SET authority_id=? WHERE user_id=?";
		int rowcount = jdbcTemplate.update(sql, authorityId, userId);
		return rowcount;
	}

	// 後台使用者管理顯示權限
	@Override
	public Authority getAuthorityById(Integer authorityId) {
		String sql = "SELECT authority_id, authority_name FROM authorities WHERE authority_id=?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Authority.class), authorityId);
	}

	@Override
	public List<Authority> findAllAuthorities() {
		String sql = "SELECT authority_id, authority_name FROM authorities";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Authority.class));
	}

	@Override
	public int updateUserPassword(String userEmail, String userPassword, String salt) {
		String sql = "UPDATE users SET user_password=?, salt=? WHERE user_email=?";
		return jdbcTemplate.update(sql, userPassword, salt, userEmail);
	}
	

}
