package com.example.demo.model.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donation {

	private Integer donationId; // 自動產生
	private String frequency; // 每月, 每年, 單筆
	// enum frequency {每月, 每年, 單筆 }; 
	private Integer amount;
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date donatedTime; // 自動產生
	@JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
	private Date endTime; // 可設定結束日期（？
	private String donateStatus; // 進行中、已完成
	
	private Integer userId;
	// private Integer newsId; // 從哪篇新聞點進來
}
