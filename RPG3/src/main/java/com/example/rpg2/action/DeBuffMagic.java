package com.example.rpg2.action;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.entity.Magic;

import lombok.Data;

@Data
public class DeBuffMagic implements TaregetEnemyAction{
	
	private MonsterData monsterData;
	private Integer damage;
	private String stratMessage;
	private String damageMessage;
	private String  notEnoughMpMessage;
	private String resultMessage;
	private Integer target;
	private AllyData allyData;
	private Magic    magic;
	
	
	public DeBuffMagic( AllyData allyData , Magic magic ) {
		this.allyData = allyData;
		this.magic = magic;
		this.stratMessage =  allyData.getName() + "は" + magic.getName() + "を放った!!";
	}
	
	
	public boolean isNotEnoughMp() {
		boolean check = magic.getMp() > allyData.getCurrentMp();
		
		if( check ) {
			this.notEnoughMpMessage = "しかしMPが足りない･･･" ;
		}
		
		return check;
	}
	
	
	//妨害魔法(状態異常とダメージを一緒に処理する攻撃が想定されるため、攻撃魔法のクラスと合体させる予定)
	@Override
	public MonsterData action( MonsterData monsterData ) {
		
		//防御妨害魔法の処理
		if( magic.getBuffcategory().equals( "DEF" )) {
			
			double def = monsterData.getCurrentDEF();
			
			//下限チェック
			if( def < 1 ) {
				this.resultMessage = monsterData.getName() + "守備力は、これ以上は下がらなかった･･･";
			
			//下限未達
			}else{
				double buffPoint = magic.getPercentage() + 1.2 ;
				def = def / buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def < 1 ) {
					def = 0 ;
				}
				monsterData.setCurrentDEF( (int) def );
				this.resultMessage = monsterData.getName() + "守備力が下がった!!";
			}
		}
		
		return monsterData;
	}

}
