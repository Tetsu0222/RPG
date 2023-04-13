package com.example.rpg2.process;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.MonsterPattern;
import com.example.rpg2.status.Burn;
import com.example.rpg2.status.Confusion;
import com.example.rpg2.status.HolyBarrier;
import com.example.rpg2.status.Hubaha;
import com.example.rpg2.status.MagicBarrier;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Paralysis;
import com.example.rpg2.status.Poison;
import com.example.rpg2.status.Sleep;
import com.example.rpg2.status.Status;
import com.example.rpg2.status.Swoon;

import lombok.Data;

@Data
public class BadStatusAlly {
	
	private String  buffMessage;
	
	//状態異常の処理
	public AllyData bad( AllyData allyData , MonsterPattern monsterPattern ) {
		
		//凍てつく波動の処理
		if( monsterPattern.getBuffcategory().equals( "wave" )) {
			allyData = this.wave( allyData, allyData.getStatusSet() );
			
		//守備力ダウン系の処理
		}else if( monsterPattern.getBuffcategory().equals( "DEF" )) {
			allyData = this.defDown( allyData );
			
		}else{

			Random random = new Random();
			
			int x = random.nextInt( 1 + allyData.getResistance() );
	
			if( allyData.getSurvival() == 2 ) {
				this.buffMessage = allyData.getName() + "は聖なる守りの加護を得ている。";
				
			//状態異常完全耐性
			}else if( allyData.getResistance() == 4 ) {
				this.buffMessage = allyData.getName() + "は状態異常を受け付けない。";
				
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
					Set<Status> statusSet = this.badStatusSleep( allyData );
					allyData = this.sleep( allyData , statusSet );
					
				//麻痺の付与
				}else if( monsterPattern.getBuffcategory().equals( "paralysis" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.paralysis( allyData , statusSet );
					
				//気絶の付与
				}else if( monsterPattern.getBuffcategory().equals( "swoon" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.swoon( allyData , statusSet );
				
				//混乱の付与
				}else if( monsterPattern.getBuffcategory().equals( "confusion" ) && allyData.getSurvival() == 1 ) {
					Set<Status> statusSet = this.badStatus( allyData );
					allyData = this.confusion( allyData , statusSet );
				}
				
			//状態異常判定が失敗
			}else{
				this.buffMessage = allyData.getName() + "は状態異常にならない";
			}
		}
		return allyData;
	}
	
	//正常状態から状態異常へ変化させる事前処理メソッド
	public Set<Status> badStatus( AllyData allyData ){
		
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		return statusSet;
	}
	
	//正常状態から睡眠へ変化させる事前処理メソッド
	public Set<Status> badStatusSleep( AllyData allyData ){
		
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ))
				.filter( s -> !s.getName().equals( "防御" ))
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
	
	//混乱の付与
	public AllyData confusion( AllyData allyData , Set<Status> statusSet ) {
		statusSet.add( new Confusion( allyData ) );
		allyData.setStatusSet( statusSet );
		this.buffMessage = allyData.getName() + "は正気を失った…";
		
		return allyData;
	}
	
	//いてつく波動の処理
	public AllyData wave( AllyData allyData , Set<Status> statusSet ) {
		
		statusSet.remove( new HolyBarrier()  );
		statusSet.remove( new MagicBarrier() );
		statusSet.remove( new Hubaha()       );
		
		if(statusSet.size() == 0 ) {
			statusSet.add( new Normal() );
		}
		
		allyData.setSurvival( 1 );
		allyData.setCurrentDEF( allyData.getDefaultDEF() );
		allyData.setCurrentATK( allyData.getDefaultATK() );
		this.buffMessage = allyData.getName() + "は加護を失った…";
		
		return allyData;
	}
	
	//防御力ダウンの処理
	public AllyData defDown( AllyData allyData ) {
		
		int down = allyData.getDefaultDEF()/10;
		int def = allyData.getCurrentDEF();
		
		def = def - down;
		
		allyData.setCurrentDEF( def );
		this.buffMessage = allyData.getName() + "は守備力が下がった!!";
		
		if( allyData.getCurrentDEF() < 0 ) {
			allyData.setCurrentDEF( 0 );
			this.buffMessage = allyData.getName() + "にこれ以上の効果はないようだ…";
		}
		
		return allyData;
	}

}
