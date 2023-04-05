package com.example.rpg2.battle;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.entity.MonsterPattern;
import com.example.rpg2.status.Dead;
import com.example.rpg2.status.Poison;
import com.example.rpg2.status.Status;

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
	private String  message ;
	private String  battleMessage;
	private String  buffMessage;
	private String  resultMessage;
	
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
		
		//攻撃する相手を乱数で決定
		Integer target = random.nextInt( targetList.size() );
		this.targetId = targetList.get( target );
		AllyData allyData = partyMap.get( targetId );
		
		//敵の攻撃方法をアナウンス
		this.message = monsterData.getName() + monsterPattern.getText();
		
		//悪性ステータス異常
		if( !monsterPattern.getBuffcategory().equals( "no" ) ) {
			if( monsterPattern.getBuffcategory().equals( "poison" ) && allyData.getSurvival() == 1 ) {
			Set<Status> statusSet = allyData.getStatusSet()
					.stream()
					.filter( s -> !s.getName().equals( "正常" ) )
					.collect( Collectors.toSet() );
			statusSet.add( new Poison( allyData ) );
			allyData.setStatusSet( statusSet );
			this.damage = 0;
			this.battleMessage = allyData.getName() + "は毒状態になった!!";
			}
		}
		
		//ダメージ補正の乱数を初期化
		Integer plusDamage = 0;
		
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() ) / 4 ;
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" ) && monsterPattern.getPercentage() == 0 ) {
			//(攻撃力-防御力/2) + 乱数 = ダメージ
			this.damage = ( monsterData.getCurrentATK() - ( allyData.getCurrentDEF() / 2 )) + plusDamage;
			
			if( damage < 0 ) {
				this.damage = 0;
				this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			}else{
				this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
			if( damage < 0 ) {
				this.damage = 0;
				this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			}else{
				this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
			}
		}

		Integer HP = allyData.getCurrentHp() - damage;
		
		if( HP <= 0 ) {
			allyData.setCurrentHp( 0 );
			allyData.setSurvival( 0 );
			Set<Status> statusSet = allyData.getStatusSet();
			statusSet.clear();
			statusSet.add( new Dead() );
			allyData.setStatusSet( statusSet );
			this.resultMessage = allyData.getName() + "は死んでしまった…";
		}else{
			allyData.setCurrentHp( HP );
		}
		
		return allyData;
	}
	
	
	//全体攻撃を処理
	public AllyData attackSkillWhole( Map<Integer,AllyData> partyMap , Integer target ) {
		
		this.targetId = target;
		AllyData allyData = partyMap.get( targetId );
		
		//悪性ステータス異常
		if( !monsterPattern.getBuffcategory().equals( "no" ) ) {
			if( monsterPattern.getBuffcategory().equals( "poison" ) && allyData.getSurvival() == 1 ) {
			Set<Status> statusSet = allyData.getStatusSet()
					.stream()
					.filter( s -> !s.getName().equals( "正常" ) )
					.collect( Collectors.toSet() );
			statusSet.add( new Poison( allyData ) );
			allyData.setStatusSet( statusSet );
			this.damage = 0;
			this.battleMessage = allyData.getName() + "は毒状態になった!!";
			}
		}
		
		Integer plusDamage = 0;
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() + 1 ) / 8
							+ random.nextInt( monsterData.getCurrentATK() ) / 8;
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" ) && monsterPattern.getPercentage() == 0 ) {
			//(攻撃力-防御力/2) + 乱数 = ダメージ
			this.damage = ( monsterData.getCurrentATK() - ( allyData.getCurrentDEF() / 2 )) + plusDamage;
			
			if( damage < 0 ) {
				this.damage = 0;
				this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			}else{
				this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
			this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
		}
		
		Integer HP = allyData.getCurrentHp() - damage;
		
		if( HP <= 0 ) {
			allyData.setCurrentHp( 0 );
			allyData.setSurvival( 0 );
			Set<Status> statusSet = allyData.getStatusSet();
			statusSet.clear();
			statusSet.add( new Dead() );
			allyData.setStatusSet( statusSet );
			this.resultMessage = allyData.getName() + "は死んでしまった…";
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
