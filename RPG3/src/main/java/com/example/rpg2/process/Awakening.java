package com.example.rpg2.process;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;


public class Awakening {
	
	
	//物理攻撃による睡眠解除
	public static AllyData awakening( AllyData allyData ){
		
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
	
	
	//物理攻撃による睡眠解除
	public static MonsterData awakening( MonsterData monsterData ){
		
		Set<Status> statusSet = monsterData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "睡眠" ))
				.collect( Collectors.toSet() );
		
		int size = statusSet.size();
		
		if( size == 0 ) {
			statusSet.add( new Normal() );
		}
		
		monsterData.setStatusSet( statusSet );
		
		return monsterData;
	}

}
