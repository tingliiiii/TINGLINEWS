package com.example.demo.service;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.dao.UserDao;
import com.example.demo.model.dto.SavedDto;
import com.example.demo.model.dto.UserDto;
import com.example.demo.model.dto.UserLoginDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.Donated;
import com.example.demo.model.po.Saved;
import com.example.demo.model.po.User;
import com.example.demo.security.PasswordUtil;
import com.example.demo.security.WebKeyUtil;

@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private FunctionService functionService;

	// 個人資訊
	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	// 個人資訊（包括收藏紀錄及贊助紀錄）
	public UserProfileDto getUserProfile(Integer userId) {

		UserProfileDto userProfile = new UserProfileDto();
		User user = userDao.getUserById(userId);

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

	// 登入：驗證密碼哈希值
	public User validateUser(String userEmail, String userPassword) {
		User user = userDao.getUserByEmail(userEmail);

		if (user != null) {
			
			// 判斷 password
			// 得到使用者的 hash 與 salt
			String hash = user.getUserPassword(); // 使用者的 hash
			byte[] salt = WebKeyUtil.hexStringToByteArray(user.getSalt()); // 使用者的 salt

			try {
				MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
				messageDigest.reset(); // 重置
				messageDigest.update(salt); // 加鹽
				
				// 根據使用者輸入的 password 與已知的 salt 來產出 inputHashed
				byte[] inputHashedBytes = messageDigest.digest(userPassword.getBytes());
				String inputHashed = WebKeyUtil.bytesToHexString(inputHashedBytes);
				
				// 比較 inputHashed（使用者輸入的）與 hash（已儲存的）是否相等
				if (inputHashed.equals(hash)) {
					return user;
				} else {
					return null;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return user;

	}

	// 修改
	public Boolean updateUser(Integer userId, User user) {
		return userDao.updateUser(userId, user) > 0;
	}

//	======================================================================

	// 後台使用者管理
	public List<UserDto> findAllUserDtos() {

		List<UserDto> userDtos = new ArrayList<>();
		// PO
		List<User> users = userDao.findAllUsers();
		// PO 轉 DTO
		for (User user : users) {
			UserDto userDto = new UserDto();
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

	public UserDto getUserDtoFromUserId(Integer userId) {
		User user = userDao.getUserById(userId);
		UserDto dto = new UserDto();
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

//	======================================================================

	// 登入頁＋後台網頁內容管理
	/*
	 * public UserLoginDto getLoginDtoById(Integer userId) { User user =
	 * userDao.getUserById(userId); UserLoginDto dto = new UserLoginDto(userId,
	 * user.getUserEmail(), user.getUserName()); return dto; }
	 */
	/*
	 * 好像不需要（被上面取代） public List<UserLoginDto> findAllLoginDtos(){ List<UserLoginDto>
	 * userDtos = new ArrayList<>(); List<User> users = userDao.findAllUsers(); for
	 * (User user : users) { UserLoginDto userDto = new UserLoginDto();
	 * userDto.setUserId(user.getUserId()); userDto.setUserName(user.getUserName());
	 * userDto.setUserEmail(user.getUserEmail()); userDtos.add(userDto); } return
	 * userDtos; }
	 */

}
