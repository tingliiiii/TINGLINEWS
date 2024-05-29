package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.User;

public interface UserDao {
	int addUser(User user);
	int updateUser(Integer userId, User user);
	int updateUserAuthority(Integer userId, Integer authorityId);
	int deleteUser(Integer userId);
	User getUserById(Integer userId);
	List<User> findAllUsers();
}