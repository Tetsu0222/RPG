package com.example.rpg2.action;

import java.util.Random;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;

import lombok.Data;

@Data
public class Attack implements TaregetEnemyAction{
	
	private MonsterData monsterData;
	private Integer damage;
	private String stratMessage;
	private String damageMessage;
	private String resultMessage;
	private Integer target;
	private AllyData allyData;
	
	//使用しない
	private String getNotEnoughMpMessage;
	private boolean isNotEnoughMp;
	
	
	public Attack( AllyData allyData ) {
		this.allyData = allyData;
		this.stratMessage =  allyData.getName() + "の攻撃!!!";
	}

	//通常攻撃
	@Override
	public MonsterData action( MonsterData monsterData ) {
		
		//会心の発生率設定
		Random random = new Random();
		int critical = random.nextInt( 255 ); //引数をキャラクターの会心率に応じて引き算し、会心発生率を上昇させる予定
		
		//非会心
		if( critical > 6 ) {
			//(攻撃力-防御力/2) + 乱数 = ダメージ
			this.damage = ( allyData.getCurrentATK() - ( monsterData.getCurrentDEF() / 2 )) 
					+ ( random.nextInt( allyData.getCurrentATK() ) / 2 );
			this.damageMessage = damage + "のダメージを与えた!!";
			
		//会心
		}else{
			//(攻撃力 * 2) + 乱数 = ダメージ
			this.damage = allyData.getCurrentATK() * 2
					+ ( random.nextInt( allyData.getCurrentATK() ) / 2 );
			this.damageMessage = "会心の一撃!!!" + damage + "のダメージを与えた!!";
		}
		
		if( damage < 0 ) {
			damage = 0;
		}
		
		Integer HP = monsterData.getCurrentHp() - damage;
		
		if( HP < 0 ) {
			monsterData.setCurrentHp( 0 );
			monsterData.setSurvival( 0 );
			this.resultMessage =  monsterData.getName() + "を倒した!!";
		}else{
			monsterData.setCurrentHp( HP );
		}
		
		return monsterData;
	}


	@Override
	public String getNotEnoughMpMessage() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}




}
