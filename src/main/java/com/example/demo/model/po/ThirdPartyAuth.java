package com.example.demo.model.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThirdPartyAuth {
	
	private Integer id;
	private Integer userId;
	private String provider;
	private Integer providerUserId;
}
