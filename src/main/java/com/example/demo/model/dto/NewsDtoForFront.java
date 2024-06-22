package com.example.demo.model.dto;

import java.util.Date;
import java.util.List;

import com.example.demo.model.po.Journalist;
import com.example.demo.model.po.News;
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
	private String image;

	private Tag tag;
	private List<Journalist> journalists;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updatedTime;
	
	private boolean isPublic;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date publicTime;
	
	private List<News> relatedNews;

}
