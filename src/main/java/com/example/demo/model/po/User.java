package com.example.demo.model.po;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Integer userId; // 自動產生
	private String userName;
	@Email
	private String UserEmail; // 帳號 unique
	private String userPassword;
	private String salt;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime; // 自動產生
	private Integer authorityId;

	// 以下為 optional

	private String gender;
	private String phone;
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 前台傳給後台的格式轉換
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 後台傳給前台的格式轉換
	private LocalDate birthday; // 生日

}
