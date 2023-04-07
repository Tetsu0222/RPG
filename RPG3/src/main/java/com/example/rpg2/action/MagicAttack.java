package com.example.rpg2.action;

import java.util.Random;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.entity.Magic;

import lombok.Data;

@Data
public class MagicAttack implements TaregetEnemyAction{
	
	private MonsterData monsterData;
	private Integer damage;
	private String stratMessage;
	private String damageMessage;
	private String  notEnoughMpMessage;
	private String resultMessage;
	private Integer target;
	private AllyData allyData;
	private Magic    magic;
	
	
	public MagicAttack( AllyData allyData , Magic magic ) {
		this.allyData = allyData;
		this.magic = magic;
		this.stratMessage =  allyData.getName() + "は" + magic.getName() + "を放った!!";
	}
	
	
	//MP判定
	public boolean isNotEnoughMp() {
		boolean check = magic.getMp() <= allyData.getCurrentMp();
		
		if( !check ) {
			this.notEnoughMpMessage = "しかしMPが足りない･･･" ;
		}
		
		return !check;
	}
	
	//攻撃魔法
	@Override
	public MonsterData action( MonsterData monsterData  ) {
		
		Random random = new Random();
		
		//妨害魔法の処理
		if( !magic.getBuffcategory().equals( "no" )) {
			
			//妨害魔法を生成
			TaregetEnemyAction deBuffMagic = new DeBuffMagic( allyData , magic  );
			monsterData = deBuffMagic.action( monsterData );
			
			//妨害魔法の結果を取得
			this.resultMessage = deBuffMagic.getResultMessage();
			
		}
		
		//攻撃魔法の処理
		if( magic.getPoint() != 0 ) {
			
			//魔法威力 + 乱数 = ダメージ
			Integer damage = magic.getPoint() + ( random.nextInt( magic.getPoint() ) / 4 ) - random.nextInt( magic.getPoint() / 4 );
				
			if( damage < 0 ) {
				damage = 0;
			}
				
			Integer HP = monsterData.getCurrentHp() - damage;
			this.damageMessage = monsterData.getName() + "に" + damage + "のダメージを与えた!!";
			
			//対象が戦闘不能の場合の処理
			if( HP <= 0 ) {
				monsterData.setCurrentHp( 0 );
				monsterData.setSurvival( 0 );
				this.resultMessage = monsterData.getName() + "を倒した!!";
			
			//対象が生存している場合の処理
			}else{
				monsterData.setCurrentHp( HP );
			}
		}
		
		return monsterData;
	}

}
