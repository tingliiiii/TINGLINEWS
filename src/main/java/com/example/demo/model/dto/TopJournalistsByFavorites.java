package com.example.demo.model.dto;

import lombok.Data;

@Data
public class TopJournalistsByFavorites {
	
	private Integer  journalistId;
	private String journalistName;
	private Integer count;
	

}
