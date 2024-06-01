package com.example.demo.model.dto;

import java.util.Date;

import com.example.demo.model.po.News;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SavedDto {

	// 個人資訊頁查詢收藏紀錄
	
	private Integer savedId; 
	private Integer userId;
	private News news; 
	private Date savedTime;
}
