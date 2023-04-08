package com.example.rpg2.action.skill;

import com.example.rpg2.action.TaregetEnemyAction;
import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.entity.Skill;
import com.example.rpg2.process.BadStatusEnemy;

import lombok.Data;

@Data
public class DeBuffSkill implements TaregetEnemyAction{
	
	private MonsterData monsterData;
	private Integer damage;
	private String stratMessage;
	private String damageMessage;
	private String  notEnoughMpMessage;
	private String resultMessage;
	private Integer target;
	private AllyData allyData;
	private Skill skill;
	
	
	public DeBuffSkill( AllyData allyData , Skill skill ) {
		this.allyData = allyData;
		this.skill = skill;
		this.stratMessage =  allyData.getName() + "は" + skill.getName() + "を放った!!";
	}
	
	
	//MP判定
	public boolean isNotEnoughMp() {
		boolean check = skill.getMp() <= allyData.getCurrentMp();
		
		if( !check ) {
			this.notEnoughMpMessage = "しかしMPが足りない･･･" ;
		}
		
		return !check;
	}
	
	
	//妨害魔法
	@Override
	public MonsterData action( MonsterData monsterData ) {
		
		//防御妨害魔法の処理
		if( skill.getBuffcategory().equals( "DEF" )) {
			
			double def = monsterData.getCurrentDEF();
			
			//下限チェック
			if( def < 1 ) {
				this.resultMessage = monsterData.getName() + "の守備力は、これ以上は下がらなかった･･･";
			
			//下限未達
			}else{
				double buffPoint = skill.getPercentage() + 0.2 ;
				def = def / buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def < 1 ) {
					def = 0 ;
				}
				monsterData.setCurrentDEF( (int) def );
				this.resultMessage = monsterData.getName() + "の守備力が下がった!!";
			}
			
		}else if( skill.getBuffcategory().equals( "ATK" )) {
			
			double atk = monsterData.getCurrentATK();
			
			//下限チェック
			if( atk < 1 ) {
				this.resultMessage = monsterData.getName() + "の攻撃力は、これ以上は下がらなかった･･･";
			
			//下限未達
			}else{
				double buffPoint = skill.getPercentage() + 1.2 ;
				atk = atk / buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( atk < 1 ) {
					atk = 0 ;
				}
				
				monsterData.setCurrentDEF( (int) atk );
				this.resultMessage = monsterData.getName() + "の攻撃力が下がった!!";
			}
			
		//状態異常の処理	
		}else{
			BadStatusEnemy badStatusEnemy = new BadStatusEnemy();
			monsterData = badStatusEnemy.bad( monsterData , skill );
			this.resultMessage = badStatusEnemy.getResultMessage();
		}
		
		return monsterData;
	}


	@Override
	public void setResultMessage() {
		// TODO 自動生成されたメソッド・スタブ
		
	}
	
}
