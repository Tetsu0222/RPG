package com.example.rpg2.battle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.entity.MonsterPattern;
import com.example.rpg2.status.Burn;
import com.example.rpg2.status.Dead;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Paralysis;
import com.example.rpg2.status.Poison;
import com.example.rpg2.status.Sleep;
import com.example.rpg2.status.Status;
import com.example.rpg2.status.Swoon;

import lombok.Data;

@Data
public class EnemyAction {
	
	private String pattern;
	private String range;
	private MonsterPattern monsterPattern;
	private MonsterData monsterData;
	private Integer targetId;

	private Integer recovery;
	private Integer damage  = 0;
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
	public AllyData attackSkillSingle( Map<Integer,AllyData> partyMap , Set<Integer> targetSet ) {
		
		List<Integer> targetList = new ArrayList<Integer>( targetSet );
		
		//攻撃する相手を乱数で決定
		Integer target = random.nextInt( targetList.size() );
		this.targetId = targetList.get( target );
		AllyData allyData = partyMap.get( targetId );
		
		//敵の攻撃方法をアナウンス
		this.message = monsterData.getName() + monsterPattern.getText();
		
		//悪性ステータス異常
		if( !monsterPattern.getBuffcategory().equals( "no" ) ) {
			int x = random.nextInt( 1 + allyData.getResistance() );

			if( allyData.getSurvival() == 2 ) {
				this.buffMessage = allyData.getName() + "は聖なる守りの加護を得ている。";
				
			//状態異常完全耐性
			}else if( allyData.getResistance() == 4 ) {
			
			//状態異常判定
			}else if( x == 0 ){
				
				//毒付与
				if( monsterPattern.getBuffcategory().equals( "poison" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.poison( allyData , statusSet );
					
				//火傷付与
				}else if( monsterPattern.getBuffcategory().equals( "burn" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.burn( allyData , statusSet );
					
				//睡眠付与
				}else if( monsterPattern.getBuffcategory().equals( "sleep" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.sleep( allyData , statusSet );
					
				//麻痺の付与
				}else if( monsterPattern.getBuffcategory().equals( "paralysis" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.paralysis( allyData , statusSet );
					
				//気絶の付与
				}else if( monsterPattern.getBuffcategory().equals( "swoon" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.swoon( allyData , statusSet );
				}
			}else{
				this.buffMessage = allyData.getName() + "は状態異常にならない";
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
			
			if( this.isDefense( allyData )){
				this.damage = damage / 2;
			}
			
			if( damage < 0 ) {
				this.damage = 0;
				this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			}else{
				this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
				if( allyData.getStatusSet().stream().filter( s -> s.getName().equals( "睡眠" )).count() == 1 ) {
					this.resultMessage = allyData.getName() + "は目を覚ました!";
					allyData = this.awakening( allyData );
				}	
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
			
			if( this.isDefense( allyData )){
				this.damage = damage / 2;
			}
			
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
			int x = random.nextInt( 1 + allyData.getResistance() );

			if( allyData.getSurvival() == 2 ) {
				this.buffMessage = allyData.getName() + "は聖なる守りの加護を得ている。";
				
			//状態異常完全耐性
			}else if( allyData.getResistance() == 4 ) {
			
			//状態異常判定
			}else if( x == 0 ){
				
				//毒付与
				if( monsterPattern.getBuffcategory().equals( "poison" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.poison( allyData , statusSet );
					
				//火傷付与
				}else if( monsterPattern.getBuffcategory().equals( "burn" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.burn( allyData , statusSet );
					
				//睡眠付与
				}else if( monsterPattern.getBuffcategory().equals( "sleep" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.sleep( allyData , statusSet );
					
				//麻痺の付与
				}else if( monsterPattern.getBuffcategory().equals( "paralysis" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.paralysis( allyData , statusSet );
					
				//気絶の付与
				}else if( monsterPattern.getBuffcategory().equals( "swoon" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.swoon( allyData , statusSet );
				}
			}else{
				this.buffMessage = allyData.getName() + "は状態異常にならない";
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
			
			if( this.isDefense( allyData )){
				this.damage = damage / 2;
			}
			
			if( damage < 0 ) {
				this.damage = 0;
				this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			}else{
				this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
				if( allyData.getStatusSet().stream().filter( s -> s.getName().equals( "睡眠" )).count() == 1 ) {
					this.resultMessage = allyData.getName() + "は目を覚ました!";
					allyData = this.awakening( allyData );
				}	
			}
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
			this.damage = monsterData.getCurrentATK() + plusDamage;
			
			if( this.isDefense( allyData )){
				this.damage = damage / 2;
			}
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
	
	
	
	
	//正常から状態異常へ変化
	public Set<Status> badStatus( AllyData allyData ){
		
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		return statusSet;
	}
	
	//毒の付与
	public AllyData poison( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Poison( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は毒状態になった!!";
		
		return allyData;
	}
	
	//火傷の付与
	public AllyData burn( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Burn( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は火傷を負った!!";
		
		return allyData;
	}
	
	//睡眠の付与
	public AllyData sleep( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Sleep( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は眠りに落ちた!!";
		
		return allyData;
	}
	
	//物理攻撃による睡眠解除
	public AllyData awakening( AllyData allyData ){
		
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "睡眠" ))
				.collect( Collectors.toSet() );
		
		int size = statusSet.size();
		
		if( size == 0 ) {
			statusSet.add( new Normal() );
		}
		
		allyData.setStatusSet( statusSet );
		
		return allyData;
	}
	
	//麻痺の付与
	public AllyData paralysis( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Paralysis( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は体が動き難くなった!!";
		
		return allyData;
	}
	
	//気絶の付与
	public AllyData swoon( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Swoon( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は気を失ってしまった…";
		
		return allyData;
	}
	
	
	//防御の有無チェック
	public boolean isDefense( AllyData allyData ) {
		
		//対象者のステータス異常の中に防御が含まれているか確認(1が返れば含まれていると判定)
		Long i = allyData.getStatusSet()
				.stream()
				.filter( s -> s.getName().equals( "防御" ) )
				.count();
		
		//防御が含まれていればtureを返す。
		return i == 1;
	}
	
}
