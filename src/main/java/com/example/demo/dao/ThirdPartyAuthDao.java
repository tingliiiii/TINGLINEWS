package com.example.demo.dao;

import com.example.demo.model.po.ThirdPartyAuth;

public interface ThirdPartyAuthDao {

	int addThirdPartyAuth(ThirdPartyAuth thirdPartyAuth);
	ThirdPartyAuth findByProviderAndProviderUserId(String provider, Integer providerUserId);
}
