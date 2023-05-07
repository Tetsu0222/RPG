package com.example.rpg2.process;

import java.util.List;
import java.util.Random;

public class EnemyTarget {
	
	
	//先頭の方が狙われやすい。
	public static Integer enemyTarget( List<Integer> targetList , int size ) {
		
		Random random = new Random();
		int target = 0;
		int targetiD = 0;
		
		switch( size ) {
		
		case 4:
			
			target = random.nextInt( 10 );
			
			if( target < 4 ) {
				targetiD = targetList.get( 0 );
				
			}else if( target < 7 ){
				targetiD = targetList.get( 1 );
				
			}else if( target < 9 ){
				targetiD = targetList.get( 2 );
				
			}else if( target == 9 ){
				targetiD = targetList.get( 3 );
			}
			
			break;
			
		case 3:
			
			target = random.nextInt( 10 );
			
			if( target < 5 ) {
				targetiD = targetList.get( 0 );
				
			}else if( target < 9 ){
				targetiD = targetList.get( 1 );
					
			}else if( target == 9 ){
				targetiD = targetList.get( 2 );
			}
			
			break;
			
		case 2:
			
			target = random.nextInt( 2 );
			
			if( target == 0 ) {
				targetiD = targetList.get( 0 );
				
			}else{
				targetiD = targetList.get( 1 );
			}
			
			break;
			
		case 1:
			
			targetiD = targetList.get( 0 );
			
			break;
			
		}
		
		return targetiD;
	}

}
