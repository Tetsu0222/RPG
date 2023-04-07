package com.example.rpg2.process;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.Burn;
import com.example.rpg2.status.Paralysis;
import com.example.rpg2.status.Poison;
import com.example.rpg2.status.Sleep;
import com.example.rpg2.status.Status;
import com.example.rpg2.status.Swoon;

import lombok.Data;

@Data
public class BadStatusEnemy {
	
	
	private String resultMessage;
	
	
	public MonsterData bad( MonsterData monsterData , Magic magic ) {
		
		//Random random = new Random();
		
		//int x = random.nextInt( 1 + monsterData.getResistance() );
		
		int x = 0;
		
		if( monsterData.getSurvival() == 2 ) {
			this.resultMessage = monsterData.getName() + "は聖なる守りの加護を得ている。";
			
		//状態異常完全耐性
		//}else if( monsterData.getResistance() == 4 ) {
		
		//状態異常判定
		}else if( x == 0 ){
			
			//毒付与
			if( magic.getBuffcategory().equals( "poison" ) && monsterData.getSurvival() == 1 ) {
				Set<Status> statusSet = this.badStatus( monsterData );
				monsterData = this.poison( monsterData , statusSet );
				
			//火傷付与
			}else if( magic.getBuffcategory().equals( "burn" ) && monsterData.getSurvival() == 1 ) {
				Set<Status> statusSet = this.badStatus( monsterData );
				monsterData = this.burn( monsterData , statusSet );
				
			//睡眠付与
			}else if( magic.getBuffcategory().equals( "sleep" ) && monsterData.getSurvival() == 1 ) {
				Set<Status> statusSet = this.badStatus( monsterData );
				monsterData = this.sleep( monsterData , statusSet );
				
			//麻痺の付与
			}else if( magic.getBuffcategory().equals( "paralysis" ) && monsterData.getSurvival() == 1 ) {
				Set<Status> statusSet = this.badStatus( monsterData );
				monsterData = this.paralysis( monsterData , statusSet );
				
			//気絶の付与
			}else if( magic.getBuffcategory().equals( "swoon" ) && monsterData.getSurvival() == 1 ) {
				Set<Status> statusSet = this.badStatus( monsterData );
				monsterData = this.swoon( monsterData , statusSet );
			}
			
		//状態異常判定が失敗
		}else{
			this.resultMessage = monsterData.getName() + "は状態異常にならない";
		}
		
		return monsterData;
	}
	
	
	//正常状態から状態異常へ変化させるメソッド
	public Set<Status> badStatus( MonsterData monsterData ){
		
		Set<Status> statusSet = monsterData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		return statusSet;
	}
	
	//毒の付与
	public MonsterData poison( MonsterData monsterData , Set<Status> statusSet ) {
		statusSet.add( new Poison( monsterData ) );
		monsterData.setStatusSet( statusSet );
		this.resultMessage = monsterData.getName() + "は毒状態になった!!";
		
		return monsterData;
	}
	
	//火傷の付与
	public MonsterData burn( MonsterData monsterData , Set<Status> statusSet ) {
		statusSet.add( new Burn( monsterData ) );
		monsterData.setStatusSet( statusSet );
		this.resultMessage = monsterData.getName() + "は火傷を負った!!";
		
		return monsterData;
	}
	
	//睡眠の付与
	public MonsterData sleep( MonsterData monsterData , Set<Status> statusSet ) {
		statusSet.add( new Sleep( monsterData ) );
		monsterData.setStatusSet( statusSet );
		this.resultMessage = monsterData.getName() + "は眠りに落ちた!!";
		
		return monsterData;
	}
	
	//麻痺の付与
	public MonsterData paralysis( MonsterData monsterData , Set<Status> statusSet ) {
		statusSet.add( new Paralysis( monsterData ) );
		monsterData.setStatusSet( statusSet );
		this.resultMessage = monsterData.getName() + "は体が動き難くなった!!";
		
		return monsterData;
	}
	
	//気絶の付与
	public MonsterData swoon( MonsterData monsterData , Set<Status> statusSet ) {
		statusSet.add( new Swoon( monsterData ) );
		monsterData.setStatusSet( statusSet );
		this.resultMessage = monsterData.getName() + "は気を失ってしまった…";
		
		return monsterData;
	}


}
