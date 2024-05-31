package com.example.demo.model.po;

import java.util.Date;
import java.util.Map;

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
	private String tag; // 標籤需要一個表格嗎
	
	private Integer empId; // 記者
	private Date createdTime; // 自動生成
	private Date updatedTime; // 自動生成：每次修改時間都會更新
	
	private boolean isPublic; // 公開與否：只有編輯可以控制
	private Date publicTime; // 自動生成：發布時間
}
