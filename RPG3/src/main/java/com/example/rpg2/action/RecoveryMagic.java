package com.example.rpg2.action;

import java.util.Random;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Magic;

import lombok.Data;


@Data
public class RecoveryMagic {
	
	private Integer recovery;
	private String  stratMessage;
	private String  notEnoughMpMessage;
	private String  recoveryMessage;
	private AllyData allyData;
	private Magic    magic;
	
	
	//コンストラクタ 必要な情報を設定
	public RecoveryMagic( AllyData allyData , Magic magic ) {
		this.allyData = allyData;
		this.magic = magic;
		this.stratMessage =  allyData.getName() + "は" + magic.getName() + "を放った!!";
	}
	
	//MP判定
	public boolean isNotEnoughMp() {
		boolean check = magic.getMp() > allyData.getCurrentMp();
		
		if( check ) {
			this.notEnoughMpMessage = "しかしMPが足りない･･･" ;
		}
		
		return check;
	}
	
	
	
	//味方への回復魔法
	public AllyData action( AllyData receptionAllyData ) {
		
		Random random = new Random();
		
		if( magic.getPercentage() == 0 ) {
			int HP = receptionAllyData.getCurrentHp();
			this.recovery = magic.getPoint() + random.nextInt( magic.getPoint() / 4 ) - random.nextInt( magic.getPoint() / 4 );
				
			HP += recovery;
				
			if( receptionAllyData.getCurrentHp() < HP ) {
				HP = receptionAllyData.getMaxHP();
			}
			receptionAllyData.setCurrentHp( HP );
			this.recoveryMessage = receptionAllyData.getName() + "は" + recovery + "のHPを回復した!";
				
		}else if( magic.getPercentage() == 1 ){
			int HP = receptionAllyData.getMaxHP();
			receptionAllyData.setCurrentHp( HP );
			this.recoveryMessage = receptionAllyData.getName() + "は全快した!";
			
		}
		
		return receptionAllyData;
		
	}
	
}
