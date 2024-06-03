package com.example.demo.model.dto;

import java.util.Date;

import com.example.demo.model.po.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDtoForFront {

	// 前台顯示報導
	
	private Integer newsId;
	private String title;
	private String content;

	private Tag tag;
	private Integer userId;
	private String userName;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updatedTime;
	
	private boolean isPublic;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date publicTime; 
}
