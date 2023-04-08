package com.example.rpg2.action;

import java.util.Random;

import com.example.rpg2.action.magic.RecoveryMagic;
import com.example.rpg2.action.skill.RecoverySkill;
import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.entity.Skill;

public class SortingRecoveryAction {
	
	public static int actions;
	public static boolean targetRandom;
	
	
	//回復・補助開始前の生成
	public static TargetAllyAction sortingCreateRecoveryAction( AllyData allyData , Magic magic , Skill skill ) {
		
		TargetAllyAction targetAllyAction = null;
		
		//攻撃回数
		actions = 1;
		
		Random random = new Random();
		
		//魔法攻撃を生成
		if( magic != null) {
			targetRandom = false;
			actions = magic.getFrequency();
			if( actions == 0 ) {
				actions = random.nextInt( 6 ) + 1;
			}
			targetAllyAction = new RecoveryMagic( allyData , magic );
		
		//特技を生成
		}else{
			targetRandom = skill.getTarget().equals( "random" );
			actions = skill.getFrequency();
			if( actions == 0 ) {
				actions = random.nextInt( skill.getMaxfrequency() ) + 1;
			}
			targetAllyAction = new RecoverySkill( allyData , skill );
		}
		
		return targetAllyAction;
	}
}
