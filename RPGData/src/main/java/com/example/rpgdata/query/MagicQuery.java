package com.example.rpgdata.query;

import lombok.Data;

@Data
public class MagicQuery {
	
	private String name;
	private String category;
	private String range;
	private String buffcategory;
	
	
	//初回接続時に、全検索となるように空白で設定
	public MagicQuery() {
		
		this.name     = "";
		this.category = "";
		this.range    = "";
		this.buffcategory = "";
	}

}
