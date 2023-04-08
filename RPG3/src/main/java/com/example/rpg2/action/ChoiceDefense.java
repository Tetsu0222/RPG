package com.example.rpg2.action;

import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.status.Defense;
import com.example.rpg2.status.Status;

public class ChoiceDefense {
	
	public static AllyData choiceDefense( AllyData allyData ) {
		
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		statusSet.add( new Defense( allyData ) );
		allyData.setStatusSet( statusSet );
		
		return allyData;
	}
}
