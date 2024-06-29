package com.example.demo.model.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.example.demo.model.po.Donation;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {

	// 個人資訊頁
	
	private Integer userId;
	private String userName;
	private String UserEmail; 
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date birthday;
	private String gender;
	private String phone;
	
	private List<Donation> donationList;
	private List<FavoriteDto> favoriteList;

}
