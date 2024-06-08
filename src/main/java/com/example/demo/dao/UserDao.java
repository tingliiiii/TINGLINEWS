package com.example.demo.dao;

import java.util.List;

import com.example.demo.model.po.Authority;
import com.example.demo.model.po.User;

public interface UserDao {

	// 註冊
	int addUser(User user);

	// 密碼重設
	int updateUserPassword(String userEmail, String userPassword, String salt);

	// 個人頁面更新
	int updateUser(Integer userId, User user);

	// 以 userId 尋找使用者：刪除使用者、個人頁面、個人頁面更新
	User getUserById(Integer userId);

	// 以 userEmail 尋找使用者：登入認證、重設密碼
	User getUserByEmail(String userEmail);

	// 後台：使用者管理介面
	List<User> findAllUsers();

	// 後台：刪除使用者
	int deleteUser(Integer userId);

	// 後台：更新使用者權限
	int updateUserAuthority(Integer userId, Integer authorityId);

	// 後台：修改權限時使用
	Authority getAuthorityById(Integer authorityId);

	// 後台：修改權限時的選項
	List<Authority> findAllAuthorities();

}