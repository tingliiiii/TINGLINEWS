package com.example.demo.model.po;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Saved {
	
	private Integer savedId; // 自動生成序號
	private Integer userId;
	private Integer newsId;
	// private News news; // 要嗎還是DTO才要（需要文章標題及發布時間）
	private Date savedTime;
}
