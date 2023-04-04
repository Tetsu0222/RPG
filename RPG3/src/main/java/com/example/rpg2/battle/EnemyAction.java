package com.example.rpg2.battle;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.rpg2.entity.MonsterPattern;

import lombok.Data;

@Data
public class EnemyAction {
	
	private String pattern;
	private String range;
	private MonsterPattern monsterPattern;
	private MonsterData monsterData;
	private Integer targetId;

	private Integer recovery;
	private Integer damage  ;
	private String  message;
	
	Random random = new Random();
	
	public void decision( MonsterData monsterData ) {
		this.monsterData = monsterData;
		
		//モンスターの行動をリストからランダムで指定
		this.monsterPattern = monsterData.getPatternList().get( random.nextInt( monsterData.getPatternList().size() ) );
		this.pattern = monsterPattern.getCategory();
		this.range   = monsterPattern.getRange();
		this.message = monsterPattern.getText();
	}
	
	
	//単体攻撃を処理
	public AllyData attackSkillSingle( Map<Integer,AllyData> partyMap , List<Integer> targetList ) {
		
		Integer target = random.nextInt( targetList.size() );
		
		this.targetId = targetList.get( target );
		AllyData allyData = partyMap.get( targetId );
		
		Integer plusDamage = 0;
		
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() + 1 ) / 8
							+ random.nextInt( monsterData.getCurrentATK() ) / 8;
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" )) {
			//(攻撃力-防御力/2) + 乱数 = ダメージ
			this.damage = ( monsterData.getCurrentATK() - ( allyData.getCurrentDEF() / 2 )) + plusDamage;
			
			if( damage < 0 ) {
				this.damage = 0;
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" )){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
		}

		Integer HP = allyData.getCurrentHp() - damage;
		
		if( HP < 0 ) {
			allyData.setCurrentHp( 0 );
			allyData.setSurvival( 0 );
			this.message = monsterData.getName() + monsterPattern.getText();
		}else{
			allyData.setCurrentHp( HP );
			this.message = monsterData.getName() + monsterPattern.getText();
		}
		
		return allyData;
	}
	
	
	//全体攻撃を処理
	public AllyData attackSkillWhole( Map<Integer,AllyData> partyMap , Integer target ) {
		
		this.targetId = target;
		AllyData allyData = partyMap.get( targetId );
		
		Integer plusDamage = 0;
		
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() + 1 ) / 8
							+ random.nextInt( monsterData.getCurrentATK() ) / 8;
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" )) {
			//(攻撃力-防御力/2) + 乱数 = ダメージ
			this.damage = ( monsterData.getCurrentATK() - ( allyData.getCurrentDEF() / 2 )) + plusDamage;
			
			if( damage < 0 ) {
				this.damage = 0;
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" )){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
		}
		
		Integer HP = allyData.getCurrentHp() - damage;
		
		if( HP < 0 ) {
			allyData.setCurrentHp( 0 );
			allyData.setSurvival( 0 );
			
		}else{
			allyData.setCurrentHp( HP );
		}
		
		return allyData;
	}
	
	
	//死亡時などの処理
	public void noAction() {
		//明示的に何も処理しない。
	}
}
