package com.example.rpg2.process;

import java.util.List;
import java.util.Random;

public class EnemyTarget {
	
	
	//先頭の方が狙われやすい。
	public static Integer enemyTarget( List<Integer> targetList , int size ) {
		
		Random random = new Random();
		int targetiD = 0;
		
		if( size == 4 ) {
			
			int target = random.nextInt( 10 );
			
			if( target < 4 ) {
				targetiD = targetList.get( 0 );
				
			}else if( target < 7 ){
				targetiD = targetList.get( 1 );
				
			}else if( target < 9 ){
				targetiD = targetList.get( 2 );
				
			}else if( target == 9 ){
				targetiD = targetList.get( 3 );
			}
			
		}else if( size == 3 ) {
			
			int target = random.nextInt( 10 );
			
			if( target < 5 ) {
				targetiD = targetList.get( 0 );
				
			}else if( target < 9 ){
				targetiD = targetList.get( 1 );
					
			}else if( target == 9 ){
				targetiD = targetList.get( 2 );
			}
			
		}else if( size == 2 ) {
			
			int target = random.nextInt( 2 );
			
			if( target == 0 ) {
				targetiD = targetList.get( 0 );
				
			}else{
				targetiD = targetList.get( 1 );
			}
			
		}else{
			targetiD = targetList.get( 0 );
		}
		
		return targetiD;
	}

}
