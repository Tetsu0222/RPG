package com.example.rpg2.action.skill;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.action.TargetAllyAction;
import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Skill;
import com.example.rpg2.status.HolyBarrier;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;

import lombok.Data;

@Data
public class BuffSkill implements TargetAllyAction{
	
	private Integer recovery;
	private String  stratMessage;
	private String  notEnoughMpMessage;
	private String  recoveryMessage;
	private AllyData allyData;
	private Skill skill;
	private String resultMessage;
	
	
	
	//コンストラクタ 必要な情報を設定
	public BuffSkill( AllyData allyData , Skill skill ) {
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
	
	//味方への補助魔法
		public AllyData action( AllyData receptionAllyData ) {
			
			//Random random = new Random(); どこかで使うかも
			
			//防御補助魔法の処理
			if( skill.getBuffcategory().equals( "DEF" )) {
				
				double def = receptionAllyData.getCurrentDEF();
				
				//上昇上限チェック
				if( def >= receptionAllyData.getDefaultDEF() * 2 ) {
					this.resultMessage = receptionAllyData.getName() + "は、これ以上は守備力が上がらなかった･･･";
				
				//上限未達
				}else{
					double buffPoint = skill.getPercentage();
					def = def * buffPoint;
					
					//補正値が上限を上回らないように再分岐
					if( def > receptionAllyData.getDefaultDEF() * 2 ) {
						def = receptionAllyData.getDefaultDEF() * 2 ;
					}
					receptionAllyData.setCurrentDEF( (int) def );
					this.resultMessage = receptionAllyData.getName() + "の守備力が上がった!!";
				}
			
				
			//攻撃補助魔法
			}else if( skill.getBuffcategory().equals( "ATK" )) {
				
				double atk = receptionAllyData.getCurrentATK();
				
				//上昇上限チェック
				if( atk > receptionAllyData.getDefaultATK() * 2 ) {
					this.resultMessage = receptionAllyData.getName() + "は、これ以上は攻撃力が上がらなかった･･･";
					
				//上限未達
				}else{
					double buffPoint = skill.getPercentage();
					atk = atk * buffPoint;
					
					//補正値が上限を上回らないように再分岐
					if( atk > receptionAllyData.getDefaultATK() * 2 ) {
						atk = receptionAllyData.getDefaultATK() * 2;
					}
					receptionAllyData.setCurrentDEF( (int) atk );	
					this.resultMessage = receptionAllyData.getName() + "の攻撃力が大きく上がった!!";
				}
			
			//聖なる守り
			}else if( skill.getBuffcategory().equals( "holybarrier" ) ) {
				Set<Status> statusSet = this.goodStatus( receptionAllyData );
				statusSet.add( new HolyBarrier( receptionAllyData ) );
				receptionAllyData.setStatusSet( statusSet );
				receptionAllyData.setSurvival( 2 );
				this.resultMessage = receptionAllyData.getName() + "は聖なる守りに包まれる。";
			
				
			//毒を治す魔法
			}else if( skill.getBuffcategory().equals( "poison" ) ) {
				
				//対象キャラクターの状態異常を取得
				Set<Status> statusSet = receptionAllyData.getStatusSet();
				
				//毒状態かチェック
				Long sts = statusSet.stream()
						.filter( s -> s.getName().equals( "毒" ))
						.count();
				int size = statusSet.size();
				
				//状態異常が毒のみ
				if( sts == 1 && size == 1 ) {
					statusSet.clear();
					statusSet.add( new Normal() );
					this.resultMessage = receptionAllyData.getName() + "の毒が治った!!";
					
				//状態異常が毒以外にもある。
				}else if( sts == 1 && size > 1 ) {
					statusSet = allyData.getStatusSet()
							.stream()
							.filter( s -> !s.getName().equals( "毒" ) )
							.collect( Collectors.toSet() );
					this.resultMessage = receptionAllyData.getName() + "の毒が治った!!";
					
				//毒状態ではない。
				}else{
					this.resultMessage = receptionAllyData.getName() + "に効果はなかった…";
				}
				
				receptionAllyData.setStatusSet( statusSet );
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
