package com.example.rpg2.status;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;


public class Poison implements Status{
	
	private String name = "毒";
	private Integer damage;
	private String message;
	private String targetName;
	
	
	public int hashCode() {
		return name.hashCode();
	}
	
	public boolean equals( Object obj ) {
		
		return this.hashCode() == obj.hashCode();
	}
	
	
	public Poison( AllyData allyData ) {
		this.damage = allyData.getMaxHP() / 20 ;
		this.targetName = allyData.getName();
	}
	
	public Poison( MonsterData monsterData ) {
		this.damage = monsterData.getMaxHP() / 20 ;
		this.targetName = monsterData.getName();
	}
	
	@Override
	public Integer actionStatusBefore() {
		//no
		return 0;
	}
	
	@Override
	public Integer actionStatusAfter() {
		
		return damage;
	}
	
	@Override
	public String statusMessageAfter() {
		
		this.message = targetName + "は毒により" + damage + "ダメージを受けた";
		
		return message;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}
	
	
	

}
