package com.example.rpgdata.query;

import lombok.Data;

@Data
public class AllyQuery {
	
	private String name;
	
	/*
	private Integer hp;
	private Integer mp;
	private Integer atk;
	private Integer def;
	private Integer spe;
	private String magic;
	private String skill;
	private Integer resistance;
	private String turnstartskill;
	private String turnendskill;
	*/
	
	
	//初回接続時に、全検索となるように空白で設定
	public AllyQuery() {
		this.name = "";
	}

}
