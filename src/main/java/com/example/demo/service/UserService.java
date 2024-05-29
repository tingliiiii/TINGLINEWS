package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.po.User;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	public List<User> findAllUsers() {
		return userDao.findAllUsers();
	}

	public List<UserDto> findAllUserDtos() {

		List<UserDto> userDtos = new ArrayList<>();
		// PO
		List<User> users = findAllUsers();
		// PO 轉 DTO
		for (User user : users) {
			UserDto userDto = new UserDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			userDto.setUserEmail(user.getUserEmail());
			userDto.setGender(user.getGender());
			userDto.setBirthday(user.getBirthday());
			userDto.setPhone(user.getPhone());
			userDto.setRegisteredDate(user.getRegisteredDate());
			userDto.setAuthorityId(user.getAuthorityId());
			userDtos.add(userDto);
		}

		return userDtos;
	}

	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	public Integer addUser(User user) {
		Integer userId = userDao.addUser(user);
		return userId;
	}

	public Boolean updateUser(Integer userId, User user) {
		return userDao.updateUser(userId, user) > 0;
	}

	public Boolean updateUserAuthority(Integer userId, Integer authorityId) {
		return userDao.updateUserAuthority(userId, authorityId) > 0;
	}

	public Boolean deleteUser(Integer userId) {
		return userDao.deleteUser(userId) > 0;
	}

}
