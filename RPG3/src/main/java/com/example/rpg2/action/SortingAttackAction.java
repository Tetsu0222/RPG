package com.example.rpg2.action;

import java.util.Random;

import com.example.rpg2.action.magic.MagicAttack;
import com.example.rpg2.action.skill.SkillAttack;
import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.entity.Skill;


public class SortingAttackAction {
	
	
	public static int actions;
	public static boolean targetRandom;
	
	
	//攻撃開始前の生成
	public static TaregetEnemyAction sortingCreateAttackAction( AllyData allyData , Magic magic , Skill skill ) {
		
		TaregetEnemyAction taregetEnemyAction = null;
		
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
			taregetEnemyAction = new MagicAttack( allyData , magic );
		
		//特技を生成
		}else{
			targetRandom = skill.getTarget().equals( "random" );
			actions = skill.getFrequency();
			if( actions == 0 ) {
				actions = random.nextInt( skill.getMaxfrequency() ) + 1;
			}
			taregetEnemyAction = new SkillAttack( allyData , skill );
		}
		
		return taregetEnemyAction;
	}
	
	
	//攻撃中の再生成
	public static TaregetEnemyAction sortingRegenerationAttackAction( AllyData allyData , Magic magic , Skill skill ) {
		
		TaregetEnemyAction taregetEnemyAction = null;
		
		//魔法攻撃を生成
		if( magic != null) {
			taregetEnemyAction = new MagicAttack( allyData , magic );
		
		//特技を生成
		}else{
			taregetEnemyAction = new SkillAttack( allyData , skill );
		}
		
		return taregetEnemyAction;
	}

}
