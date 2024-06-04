package com.example.demo.model.po;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class News {

	private Integer newsId;
	private String title;
	private String content;
	
	private Integer tagId;
	private Integer userId; // 記者
	
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date createdTime; // 自動生成
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date updatedTime; // 自動生成：每次修改時間都會更新
	
	private boolean isPublic; // 公開與否：只有編輯可以控制
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date publicTime; // 自動生成：發布時間
	
	private String image;
}
