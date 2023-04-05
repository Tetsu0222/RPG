package com.example.rpg2.status;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;

public class Sleep implements Status{
	
	private String name = "睡眠";
	private String targetName;
	private Integer count;
	
	
	public int hashCode() {
		
		return name.hashCode();
	}
	
	public boolean equals( Object obj ) {
		
		return this.hashCode() == obj.hashCode();
	}
	
	
	public Sleep( AllyData allyData ) {
		this.targetName = allyData.getName();
		this.count = 3;
	}
	
	public Sleep( MonsterData monsterData ) {
		this.targetName = monsterData.getName();
		this.count = 3;
	}
	
	@Override
	public Integer actionStatusBefore() {

		return 1;
	}
	
	@Override
	public String statusMessageBefore() {
		
		return targetName + "は眠っていて動けない";
	}
	
	@Override
	public Integer actionStatusAfter() {
		//no
		return 0;
	}
	
	@Override
	public String statusMessageAfter() {
		//no
		return "no";
	}
	
	@Override
	public Integer countDown() {
		
		if( count < 0 ) {
			this.count = 0;
			
		}else {
			this.count -= 1;
		}
		
		return count;
	}
	
	@Override
	public String recoverymessage() {
		
		return targetName + "は目を覚ました!!";
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

}
