package com.example.rpg2.action;

import com.example.rpg2.battle.MonsterData;

public interface TaregetEnemyAction {
	
	public String getStratMessage();
	public String getNotEnoughMpMessage();
	public String getResultMessage();
	
	public boolean isNotEnoughMp();
	public MonsterData action( MonsterData monsterData );
	public String getDamageMessage();
	


}
