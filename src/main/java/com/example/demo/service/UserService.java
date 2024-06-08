package com.example.demo.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.dto.SavedDto;
import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.Donated;
import com.example.demo.model.po.Saved;
import com.example.demo.model.po.User;
import com.example.demo.security.PasswordUtil;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private FunctionService functionService;

	// 註冊：進行密碼加鹽與哈希
	public Integer addUser(User user) {
		// 隨機鹽值（Hex）
		String salt = PasswordUtil.generateSalt();
		try {
			// 密碼加鹽儲存
			String hashedPassword = PasswordUtil.hashPassword(user.getUserPassword(), salt);
			user.setSalt(salt);
			user.setUserPassword(hashedPassword);
			return userDao.addUser(user);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	// 重設密碼、驗證密碼哈希值
	public User getUserByEmail(String userEmail) {
		return userDao.getUserByEmail(userEmail);
	}
	
	// 重設密碼
	public Boolean resetPassword(String userEmail, String userPassword) {
		User user = getUserByEmail(userEmail);
		if(user==null) {
			return false;
		}
		String salt = PasswordUtil.generateSalt();
		try {
			String hashedPassword = PasswordUtil.hashPassword(user.getUserPassword(), salt);
			return userDao.updateUserPassword(userEmail, hashedPassword, salt)>0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	// 登入：驗證密碼哈希值
	public User validateUser(String userEmail, String userPassword) {
		User user = getUserByEmail(userEmail);

		if (user == null) {
			return null;
		}
		// 判斷 password
		// 得到使用者的 hash 與 salt
		String hash = user.getUserPassword();
		byte[] salt = PasswordUtil.hexStringToByteArray(user.getSalt());

		try {
			MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
			messageDigest.reset(); // 重置
			messageDigest.update(salt); // 加鹽

			// 根據使用者輸入的 password 與已知的 salt 來產出 inputHashed
			byte[] inputHashedBytes = messageDigest.digest(userPassword.getBytes());
			String inputHashed = PasswordUtil.bytesToHex(inputHashedBytes);

			// 比較 inputHashed（使用者輸入的）與 hash（已儲存的）是否相等
			if (inputHashed.equals(hash)) {
				return user;
			} else {
				return null;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	return user;
	}

	// 網頁內容管理 findAllNewsForBack
	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	// 個人資訊（包括收藏紀錄及贊助紀錄）
	public UserProfileDto getUserProfile(Integer userId) {

		UserProfileDto userProfile = new UserProfileDto();
		User user = getUserById(userId);

		userProfile.setUserId(user.getUserId());
		userProfile.setUserName(user.getUserName());
		userProfile.setUserEmail(user.getUserEmail());
		userProfile.setBirthday(user.getBirthday());
		userProfile.setGender(user.getGender());
		userProfile.setPhone(user.getPhone());

		List<Donated> donateds = functionService.findDonatedById(userId);
		userProfile.setDonatedList(donateds);
		List<SavedDto> saveds = functionService.findSavedById(userId);
		userProfile.setSavedList(saveds);

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
			UserAdminDto userDto = new UserAdminDto();
			userDto.setUserId(user.getUserId());
			userDto.setUserName(user.getUserName());
			userDto.setUserEmail(user.getUserEmail());
			userDto.setRegisteredTime(user.getRegisteredTime());
			Authority authority = userDao.getAuthorityById(user.getAuthorityId());
			userDto.setAuthority(authority);
			userDtos.add(userDto);
		}

		return userDtos;
	}

	public UserAdminDto getUserAdminDtoFromUserId(Integer userId) {
		User user = userDao.getUserById(userId);
		UserAdminDto dto = new UserAdminDto();
		dto.setUserId(userId);
		dto.setUserName(user.getUserName());
		dto.setUserEmail(user.getUserEmail());
		dto.setRegisteredTime(user.getRegisteredTime());
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

}
