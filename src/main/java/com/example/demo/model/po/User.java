package com.example.demo.model.po;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

	private Integer userId; // 自動產生
	
	private String userName;

	@Email(message = "Email格式不正確")
	@NotBlank(message = "Email不可空白")
	private String UserEmail; // 帳號 unique

	@NotBlank(message = "密碼不可空白")
	private String userPassword;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date registeredTime; // 自動產生

	// 以下為 optional

	@Past(message = "生日必須是過去時間")
	@DateTimeFormat(pattern = "yyyy-MM-dd") // 前台傳給後台的格式轉換
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") // 後台傳給前台的格式轉換
	private LocalDate birthday; // 生日
	
	private String gender;
	
	@Pattern(regexp = "09\\d{2}-\\d{6}", message = "電話格式不正確")
	private String phone;
		
	// 以下為 emp
	private Integer authorityId;

}
