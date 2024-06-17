package com.example.demo.model.dto;

import lombok.Data;

@Data
public class TopNewsByFavorites {

	private Integer newsId;
	private String title;
	private Integer count;
}
