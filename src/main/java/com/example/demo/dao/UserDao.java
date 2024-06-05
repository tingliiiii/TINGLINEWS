package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Authority;
import com.example.demo.model.po.User;

public interface UserDao {
	int addUser(User user);
	int updateUser(Integer userId, User user);
	int deleteUser(Integer userId);
	User getUserById(Integer userId);
	User getUserByEmail(String userEmail);
	List<User> findAllUsers();
	
	int updateUserAuthority(Integer userId, Integer authorityId);
	Authority getAuthorityById(Integer authorityId);
	List<Authority> findAllAuthorities();
}