package com.example.rpg2.battle;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.HolyBarrier;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;

import lombok.Data;

@Data
public class Action {
	
	private Integer recovery;
	private Integer damage  ;
	private String  damageMessage;
	private String  recoveryMessage;
	private String  buffMessage;
	
	Random random = new Random();
	
	//攻撃魔法
	public MonsterData actionAttackMagic( AllyData allyData , MonsterData monsterData , Magic magic ) {
		
		//魔法威力 + 乱数 = ダメージ
		Integer damage = magic.getPoint() + ( random.nextInt( magic.getPoint() ) / 4 ) - random.nextInt( magic.getPoint() / 4 );
			
		if( damage < 0 ) {
			damage = 0;
		}
			
		Integer HP = monsterData.getCurrentHp() - damage;
		this.damageMessage = damage + "のダメージを与えた!!";
			
		if( HP < 0 ) {
			monsterData.setCurrentHp( 0 );
		}else{
			monsterData.setCurrentHp( HP );
		}
		
		return monsterData;
	}
	
	
	//味方への補助魔法
	public AllyData actionBuffmagicMagic( AllyData allyData , AllyData receptionAllyData , Magic magic ) {
		
		//防御補助魔法の処理(スカラ スクルト)
		if( magic.getBuffcategory().equals( "DEF" )) {
			
			double def = receptionAllyData.getCurrentDEF();
			
			//上昇上限チェック
			if( def >= receptionAllyData.getDefaultDEF() * 2 ) {
				this.buffMessage = "は、これ以上は守備力が上がらなかった･･･";
			
			//上限未達
			}else{
				double buffPoint = magic.getPercentage() + 1.2 ;
				def = def * buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def > receptionAllyData.getDefaultDEF() * 2 ) {
					def = receptionAllyData.getDefaultDEF() * 2 ;
				}
				receptionAllyData.setCurrentDEF( (int) def );
				this.buffMessage = "の守備力が上がった!!";
			}

		//攻撃補助魔法(バイキルト)
		}else if( magic.getBuffcategory().equals( "ATK" )) {
			
			double atk = receptionAllyData.getCurrentATK();
			
			//上昇上限チェック
			if( atk > receptionAllyData.getDefaultATK() * 2 ) {
				this.buffMessage = "は、これ以上は攻撃力が上がらなかった･･･";
				
			//上限未達
			}else{
				double buffPoint = magic.getPercentage();
				atk = atk * buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( atk > receptionAllyData.getDefaultATK() * 2 ) {
					atk = receptionAllyData.getDefaultATK() * 2;
				}
				receptionAllyData.setCurrentDEF( (int) atk );	
				this.buffMessage = "の攻撃力が大きく上がった!!";
			}
		
		//毒治癒
		}else if( magic.getBuffcategory().equals( "poison" ) ) {
				
			Set<Status> statusSet = receptionAllyData.getStatusSet();
				
			Long sts = statusSet.stream()
					.filter( s -> s.getName().equals( "毒" ))
					.count();
			int size = statusSet.size();
			
			//状態異常が毒のみ
			if( sts == 1 && size == 1 ) {
				statusSet.clear();
				statusSet.add( new Normal() );
				this.buffMessage = "の毒が治った!!";
				
			//状態異常が毒以外にもある。
			}else if( sts == 1 && size > 1 ) {
				statusSet = allyData.getStatusSet()
						.stream()
						.filter( s -> !s.getName().equals( "毒" ) )
						.collect( Collectors.toSet() );
				this.buffMessage = "の毒が治った!!";
			//毒状態じゃない。
			}else{
				this.buffMessage = "に効果はなかった…";
			}
			
			receptionAllyData.setStatusSet( statusSet );

		}else if( magic.getBuffcategory().equals( "holybarrier" ) ) {
			Set<Status> statusSet = this.goodStatus( receptionAllyData );
			statusSet.add( new HolyBarrier( receptionAllyData ) );
			receptionAllyData.setStatusSet( statusSet );
			receptionAllyData.setSurvival( 2 );
			this.buffMessage = "は聖なる守りに包まれる。";
		}
		return receptionAllyData;
		
	}
	
	
	//味方への蘇生魔法
	public AllyData actionResuscitationMagic( AllyData allyData , AllyData receptionAllyData , Magic magic ) {
		
		if( magic.getPercentage() == 1 ) {
			int HP = receptionAllyData.getMaxHP();
			receptionAllyData.setCurrentHp( HP );
			receptionAllyData.resuscitation();
			this.recoveryMessage = "完全に生き返った!!";
			
		}else{
			
			//蘇生判定処理
			int judgement = random.nextInt( 3 );
			if( judgement > 0 ) {
				int HP = receptionAllyData.getMaxHP() / 2;
				receptionAllyData.setCurrentHp( HP );
				receptionAllyData.resuscitation();
				this.recoveryMessage = "生き返った!!";
			}else{
				this.recoveryMessage = "生き返らなかった･･･";
			}
		}
		
		return receptionAllyData;
		
	}
	
	
	//妨害魔法
	public MonsterData debuffMagicMagic( AllyData allyData , MonsterData monsterData , Magic magic) {
		
		//防御妨害魔法の処理
		if( magic.getBuffcategory().equals( "DEF" )) {
			
			double def = monsterData.getCurrentDEF();
			
			//下限チェック
			if( def < 1 ) {
				this.buffMessage = "守備力は、これ以上は下がらなかった･･･";
			
			//下限未達
			}else{
				double buffPoint = magic.getPercentage() + 1.2 ;
				def = def / buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def < 1 ) {
					def = 0 ;
				}
				monsterData.setCurrentDEF( (int) def );
				this.buffMessage = "守備力が下がった!!";
			}
		}
		
		return monsterData;
	}
	

	//MP消費処理
	public AllyData consumptionMp( AllyData allyData , Magic magic ) {
		
		int MP = allyData.getCurrentMp();
		MP -= magic.getMp();
		allyData.setCurrentMp( MP );
		
		return allyData;
	}
	
	
	//死亡時の処理
	public void noAction() {
		//明示的に何も処理しない。
	}
	
	//正常からへバフ状態（期限付き）へ変化
	public Set<Status> goodStatus( AllyData receptionAllyData ){
		
		Set<Status> statusSet = receptionAllyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		return statusSet;
	}
	

}
