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
import com.example.demo.model.po.Donated;
import com.example.demo.model.po.Saved;
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
		String sql = "INSERT INTO user(user_name, user_email, user_password, birthday, gender, phone) "
				+ "values(:userName, :userEmail, :userPassword, :birthday, :gender, :phone)";
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
		String sql = "UPDATE user SET user_name=:userName, user_email=:userEmail, user_password=:userPassword, "
				+ "birthday=:birthday, gender=:gender, phone=:phone WHERE user_id=:userId";
		BeanPropertySqlParameterSource params = new BeanPropertySqlParameterSource(user);
		int rowcount = namedParameterJdbcTemplate.update(sql, params);
		return rowcount;
	}
	
	// 刪除使用者（管理員）
	@Override
	public int deleteUser(Integer userId) {
		String sql = "DELETE FROM user WHERE user_id=?";
		return jdbcTemplate.update(sql, userId);
	}

	// profile、後台修改刪除
	@Override
	public User getUserById(Integer userId) {
		String sql = "SELECT user_id, user_name, user_email, birthday, gender, phone, authority_id FROM user WHERE user_id=?";
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
		String sql = "SELECT user_id, user_name, user_email, user_password, birthday, gender, phone, authority_id FROM user WHERE user_email = ?";
		try {
			// System.out.println(userEmail);
			return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(User.class), userEmail);
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	// 後台：使用者管理頁面
	@Override
	public List<User> findAllUsers() {
		String sql = "SELECT user_id, user_name, user_email, authority_id, registered_time FROM user";
		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(User.class));
	}

	// 更新權限（管理員）
	@Override
	public int updateUserAuthority(Integer userId, Integer authorityId) {
		String sql = "UPDATE user SET authority_id=? WHERE user_id=?";
		int rowcount = jdbcTemplate.update(sql, authorityId, userId);
		return rowcount;
	}

	// 後台使用者管理顯示權限
	@Override
	public Authority getAuthorityById(Integer authorityId) {
		String sql = "SELECT authority_id, authority_name FROM authority WHERE authority_id=?";
		return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Authority.class), authorityId);
	}
	
}
