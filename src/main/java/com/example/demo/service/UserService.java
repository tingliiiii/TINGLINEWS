package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.User;
import com.example.demo.security.PasswordUtil;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private ModelMapper modelMapper;

	// 密碼加鹽
	private String hashPassword(String password, String salt) throws Exception {
		return PasswordUtil.hashPassword(password, salt);
	}

	// 註冊
	public Integer addUser(User user) throws Exception {
		// 隨機鹽值（Hex）
		String salt = PasswordUtil.generateSalt();
		// 密碼加鹽儲存
		String hashedPassword = hashPassword(user.getUserPassword(), salt);
		user.setSalt(salt);
		user.setUserPassword(hashedPassword);
		return userDao.addUser(user);
	}

	// 重設密碼
	public Boolean resetPassword(String userEmail, String userPassword) throws Exception {
		User user = getUserByEmail(userEmail);
		if (user == null)
			return false;

		String salt = PasswordUtil.generateSalt();
		String hashedPassword = hashPassword(userPassword, salt);
		return userDao.updateUserPassword(userEmail, hashedPassword, salt) > 0;
	}

	// 登入：驗證密碼哈希值
	public User validateUser(String userEmail, String userPassword) throws Exception {
		User user = getUserByEmail(userEmail);
		if (user == null)
			return null;

		String hash = user.getUserPassword();
		String salt = user.getSalt();
		String inputHashed = hashPassword(userPassword, salt);

		// 比較 inputHashed（使用者輸入的）與 hash（資料庫的）是否相等
		if (inputHashed.equals(hash)) {
			return user;
		} else {
			return null;
		}
	}

	// 網頁內容管理 findAllNewsForBack
	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	// 個人資訊（包括收藏紀錄及贊助紀錄）
	public UserProfileDto getUserProfile(Integer userId) {
		User user = getUserById(userId);
		UserProfileDto userProfile = modelMapper.map(user, UserProfileDto.class);
		userProfile.setDonatedList(functionService.findDonatedById(userId));
		userProfile.setSavedList(functionService.findSavedById(userId));
		return userProfile;
	}

	// 修改個人資訊
	public Boolean updateUser(Integer userId, User user) {
		return userDao.updateUser(userId, user) > 0;
	}

//	======================================================================

	// 後台使用者管理
	public List<UserAdminDto> findAllUserAdminDtos() {

		// PO
		List<User> users = userDao.findAllUsers();
		List<UserAdminDto> userDtos = new ArrayList<>();

		// PO 轉 DTO
		for (User user : users) {
			UserAdminDto userDto = modelMapper.map(user, UserAdminDto.class);
			Authority authority = userDao.getAuthorityById(user.getAuthorityId());
			userDto.setAuthority(authority);
			userDtos.add(userDto);
		}
		return userDtos;
	}

	public UserAdminDto getUserAdminDtoFromUserId(Integer userId) {
		User user = userDao.getUserById(userId);
		UserAdminDto dto = modelMapper.map(user, UserAdminDto.class);
		Authority authority = userDao.getAuthorityById(user.getAuthorityId());
		dto.setAuthority(authority);
		return dto;
	}

	// 後台修改使用者權限
	public Boolean updateUserAuthority(Integer userId, Integer authorityId) {
		return userDao.updateUserAuthority(userId, authorityId) > 0;
	}

	// 修改權限時的選項
	public List<Authority> findAllAuthorities() {
		return userDao.findAllAuthorities();
	}

	// 修改完回傳
	public Authority getAuthorityById(Integer authorityId) {
		return userDao.getAuthorityById(authorityId);
	}

	// 後台刪除使用者
	public Boolean deleteUser(Integer userId) {
		return userDao.deleteUser(userId) > 0;
	}

	public User getUserByEmail(String userEmail) {
		return userDao.getUserByEmail(userEmail);
	}

}
