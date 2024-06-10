package com.example.demo.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.example.demo.model.po.ThirdPartyAuth;

@Repository
public class ThirdPartyAuthDaoImpl implements ThirdPartyAuthDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public int addThirdPartyAuth(ThirdPartyAuth thirdPartyAuth) {
		String sql = "INSERT INTO third_party_auth(user_id, provider, provider_user_id) VALUES(?, ?, ?)";
		return jdbcTemplate.update(sql, thirdPartyAuth.getUserId(), thirdPartyAuth.getProvider(), thirdPartyAuth.getProviderUserId());
	}

	@Override
	public ThirdPartyAuth findByProviderAndProviderUserId(String provider, Integer providerUserId) {
		String sql = "SELECT id, user_id, provider, provider_user_id FROM third_party_auth WHERE provider=? AND provider_user_id=?";
		try {
			ThirdPartyAuth thirdPartyAuth = jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(ThirdPartyAuth.class), provider, providerUserId);
			return thirdPartyAuth;
		} catch (DataAccessException e) {
			e.printStackTrace();
			return null;
		}
	}

}
