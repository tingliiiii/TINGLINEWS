package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.example.demo.dao.ThirdPartyAuthDao;
import com.example.demo.dao.UserDao;
import com.example.demo.model.dto.NewsDtoForFront;
import com.example.demo.model.dto.UserAdminDto;
import com.example.demo.model.dto.UserProfileDto;
import com.example.demo.model.po.Authority;
import com.example.demo.model.po.ThirdPartyAuth;
import com.example.demo.model.po.User;
import com.example.demo.model.response.GenericTypeReference;
import com.example.demo.security.PasswordUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

	@Autowired
	private UserDao userDao;

	@Autowired
	private ThirdPartyAuthDao thirdPartyAuthDao;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String USER_ADMIN_DTOS_CACHE_KEY = "userAdminDtos";
	private static final String USER_PROFILE_CACHE_KEY_PREFIX = "userProfile:";

	// 密碼加鹽
	private String hashPassword(String password, String salt) throws Exception {
		return PasswordUtil.hashPassword(password, salt);
	}

	// 註冊
	public Integer createUser(User user) throws Exception {
		// 隨機鹽值（Hex）
		String salt = PasswordUtil.generateSalt();
		// 密碼加鹽儲存
		String hashedPassword = hashPassword(user.getUserPassword(), salt);
		user.setSalt(salt);
		user.setUserPassword(hashedPassword);
		Integer userId = userDao.addUser(user);
		if(userId!=null) {
			clearCache(USER_ADMIN_DTOS_CACHE_KEY);
		}
		return userId;
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

		// 比較 使用者輸入的密碼 與 資料庫的密碼 是否相等
		String inputHashed = hashPassword(userPassword, user.getSalt());
		return inputHashed.equals(user.getUserPassword()) ? user : null;
	}

	// 網頁內容管理 findAllNewsForBack
	public User getUserById(Integer userId) {
		return userDao.getUserById(userId);
	}

	// 個人資訊（包括收藏紀錄及贊助紀錄）
	public UserProfileDto getUserProfile(Integer userId) {
		String key = USER_PROFILE_CACHE_KEY_PREFIX + userId;
		UserProfileDto userProfile = getRedisJson(key, UserProfileDto.class);
		if (userProfile == null) {
			User user = getUserById(userId);
			userProfile = modelMapper.map(user, UserProfileDto.class);
			userProfile.setDonationList(functionService.findDonationsByUserId(userId));
			userProfile.setFavoriteList(functionService.findFavoriteByUserId(userId));
			setRedisJson(key, userProfile);
		}
		return userProfile;
	}

	// 修改個人資訊
	public Boolean updateUserDetails(Integer userId, UserProfileDto userProfile) {
		User user = getUserById(userId);
		user.setUserName(userProfile.getUserName());
		user.setUserEmail(userProfile.getUserEmail());
		user.setBirthday(userProfile.getBirthday());
		user.setGender(userProfile.getGender());
		user.setPhone(userProfile.getPhone());
		Boolean result = userDao.updateUser(userId, user) > 0;
		if (result) {
			// 更新 Redis 緩存
			clearCache(USER_ADMIN_DTOS_CACHE_KEY);
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + userId);
		}
		return result;
	}

//	======================================================================

	// 後台使用者管理
	public List<UserAdminDto> findAllUserAdminDtos() {

		List<UserAdminDto> userDtos = getRedisList(USER_ADMIN_DTOS_CACHE_KEY, UserAdminDto.class);
		if (userDtos == null) {
			// PO
			List<User> users = userDao.findAllUsers();
			userDtos = new ArrayList<>();
			// PO 轉 DTO
			for (User user : users) {
				UserAdminDto userDto = modelMapper.map(user, UserAdminDto.class);
				Authority authority = userDao.getAuthorityById(user.getAuthorityId());
				userDto.setAuthority(authority);
				userDtos.add(userDto);
			}
			setRedisList(USER_ADMIN_DTOS_CACHE_KEY, userDtos);
		}
		return userDtos;
	}

	public UserAdminDto getUserAdminDtoById(Integer userId) {
		User user = userDao.getUserById(userId);
		UserAdminDto dto = modelMapper.map(user, UserAdminDto.class);
		Authority authority = userDao.getAuthorityById(user.getAuthorityId());
		dto.setAuthority(authority);
		return dto;
	}

	// 後台修改使用者權限
	public Boolean updateUserAuthority(Integer userId, Integer authorityId) {
		Boolean result = userDao.updateUserAuthority(userId, authorityId) > 0;
		if (result) {
			clearCache(USER_ADMIN_DTOS_CACHE_KEY);
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + userId);
		}
		return result;
	}

	// 修改權限時的選項
	public List<Authority> getAllAuthorities() {
		return userDao.findAllAuthorities();
	}

	// 修改完回傳
	public Authority getAuthorityById(Integer authorityId) {
		return userDao.getAuthorityById(authorityId);
	}

	// 後台刪除使用者
	public Boolean removeUser(Integer userId) {
		Boolean result = userDao.deleteUser(userId) > 0;
		if (result) {
			clearCache(USER_ADMIN_DTOS_CACHE_KEY);
			clearCache(USER_PROFILE_CACHE_KEY_PREFIX + userId);
		}
		return result;
	}

	public User getUserByEmail(String userEmail) {
		return userDao.getUserByEmail(userEmail);
	}

	public User findOrCreateUser(String provider, Integer providerUserId, String email, String name) {
		ThirdPartyAuth thirdPartyAuth = thirdPartyAuthDao.findByProviderAndProviderUserId(provider, providerUserId);
		if (thirdPartyAuth != null) {
			User user = userDao.getUserById(thirdPartyAuth.getUserId());
			return user;
		}
		User user = new User();
		user.setUserEmail(email);
		user.setUserName(name);
		Integer userId = userDao.addUser(user);
		thirdPartyAuth = new ThirdPartyAuth();
		thirdPartyAuth.setUserId(userId);
		thirdPartyAuth.setProvider(provider);
		thirdPartyAuth.setProviderUserId(providerUserId);
		Boolean state = thirdPartyAuthDao.addThirdPartyAuth(thirdPartyAuth) > 0;
		if (state) {
			return user;
		}
		return null;
	}

	// Redis ============================================================

	private void clearCache(String cacheKey) {
		if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
			redisTemplate.delete(cacheKey);
			log.debug("Cleared cache for key: " + cacheKey);
		}
	}

	private <T> void setRedisList(String key, List<T> list) {
		try {
			String value = objectMapper.writeValueAsString(list);
			redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Failed to set Redis list for key: " + key, e);
		}
	}

	private void setRedisJson(String key, Object object) {
		try {
			String value = objectMapper.writeValueAsString(object);
			redisTemplate.opsForValue().set(key, value, 1, TimeUnit.HOURS);
		} catch (Exception e) {
			log.error("Failed to set Redis JSON for key: " + key, e);
		}
	}

	private <T> List<T> getRedisList(String key, Class<T> elementType) {
		String json = (String) redisTemplate.opsForValue().get(key);
		if (json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, new GenericTypeReference<>(elementType));
		} catch (Exception e) {
			log.error("Failed to process JSON for key: " + key, e);
			return null;
		}
	}

	private <T> T getRedisJson(String key, Class<T> clazz) {
		String json = (String) redisTemplate.opsForValue().get(key);
		if (json == null) {
			return null;
		}
		try {
			return objectMapper.readValue(json, clazz);
		} catch (Exception e) {
			log.error("Failed to process JSON for key: " + key, e);
			return null;
		}
	}

}
