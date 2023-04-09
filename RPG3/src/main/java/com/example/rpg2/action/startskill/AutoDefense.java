package com.example.rpg2.action.startskill;

import java.util.Random;

import com.example.rpg2.action.ChoiceDefense;
import com.example.rpg2.battle.AllyData;

public class AutoDefense implements StartSkill{
	
	
	public AllyData action( AllyData allyData ) {
		
		Random random = new Random();
		
		int juds = random.nextInt( 2 );
		
		if( juds > 0 ) {
			allyData = ChoiceDefense.choiceDefense( allyData );
			allyData.setStartSkillMessage( allyData.getName() +  "はオートガードを発動!" );
			System.out.println( "ok2" );
		}
		
		
		return allyData;
	}

}
