package com.example.rpg2.action;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.HolyBarrier;
import com.example.rpg2.status.Status;

import lombok.Data;

@Data
public class BuffMagic implements TargetAllyAction{
	
	private Integer recovery;
	private String  stratMessage;
	private String  notEnoughMpMessage;
	private String  recoveryMessage;
	private AllyData allyData;
	private Magic    magic;
	private String resultMessage;
	
	
	//コンストラクタ 必要な情報を設定
	public BuffMagic( AllyData allyData , Magic magic ) {
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
	
	//味方への補助魔法
	public AllyData action( AllyData receptionAllyData ) {
		
		//Random random = new Random(); どこかで使うかも
		
		//防御補助魔法の処理(スカラ スクルト)
		if( magic.getBuffcategory().equals( "DEF" )) {
			
			double def = receptionAllyData.getCurrentDEF();
			
			//上昇上限チェック
			if( def >= receptionAllyData.getDefaultDEF() * 2 ) {
				this.recoveryMessage = receptionAllyData.getName() + "は、これ以上は守備力が上がらなかった･･･";
			
			//上限未達
			}else{
				double buffPoint = magic.getPercentage() + 1.2 ;
				def = def * buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def > receptionAllyData.getDefaultDEF() * 2 ) {
					def = receptionAllyData.getDefaultDEF() * 2 ;
				}
				receptionAllyData.setCurrentDEF( (int) def );
				this.recoveryMessage = receptionAllyData.getName() + "の守備力が上がった!!";
			}
		
			
		//攻撃補助魔法(バイキルト)
		}else if( magic.getBuffcategory().equals( "ATK" )) {
			
			double atk = receptionAllyData.getCurrentATK();
			
			//上昇上限チェック
			if( atk > receptionAllyData.getDefaultATK() * 2 ) {
				this.recoveryMessage = receptionAllyData.getName() + "は、これ以上は攻撃力が上がらなかった･･･";
				
			//上限未達
			}else{
				double buffPoint = magic.getPercentage();
				atk = atk * buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( atk > receptionAllyData.getDefaultATK() * 2 ) {
					atk = receptionAllyData.getDefaultATK() * 2;
				}
				receptionAllyData.setCurrentDEF( (int) atk );	
				this.recoveryMessage = receptionAllyData.getName() + "の攻撃力が大きく上がった!!";
			}
		
		//聖なる守り
		}else if( magic.getBuffcategory().equals( "holybarrier" ) ) {
			Set<Status> statusSet = this.goodStatus( receptionAllyData );
			statusSet.add( new HolyBarrier( receptionAllyData ) );
			receptionAllyData.setStatusSet( statusSet );
			receptionAllyData.setSurvival( 2 );
			this.recoveryMessage = receptionAllyData.getName() + "は聖なる守りに包まれる。";
		}
		
		return receptionAllyData;
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
