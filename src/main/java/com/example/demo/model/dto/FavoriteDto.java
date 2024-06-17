package com.example.demo.model.dto;

import java.util.Date;

import com.example.demo.model.po.News;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDto {

	// 個人資訊頁查詢收藏紀錄
	
	private Integer favoriteId; 
	private Integer userId;
	private News news; 
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date favoriteTime;
}
