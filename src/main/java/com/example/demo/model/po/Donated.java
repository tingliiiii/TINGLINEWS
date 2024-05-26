package com.example.demo.model.po;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Donated {

	private Integer donatedId; // 自動產生
	private String frequency; // 每月, 每年, 單筆
	// enum frequency {每月, 每年, 單筆 }; 
	private Double amount;
	
	private Date donatedTime; // 自動產生
	private Date endDate; // 可設定結束日期（？
	private String status; // 進行中、已完成
	
	private Integer userId;
	// private Integer newsId; // 從哪篇新聞點進來
}
