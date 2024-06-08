package com.example.demo.model.dto;

import java.util.Date;

import com.example.demo.model.po.Authority;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAdminDto {
	
	// 後台使用者管理

	private Integer userId;
	private String userName;
	private String userEmail; 
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime;
	private Authority authority;
	
}
