package com.example.rpg2.process;

import java.util.List;
import java.util.Random;

public class EnemyTarget {
	
	
	//先頭の方が狙われやすい。
	public static Integer enemyTarget( List<Integer> targetList ) {
		
		Random random = new Random();
		int targetiD = 0;
		
		int size = targetList.size();
		
		if( size == 4 ) {
			
			int target = random.nextInt( 10 );
			
			if( target < 4 ) {
				targetiD = 0;
				
			}else if( target < 7 ){
				targetiD = 1;
				
			}else if( target < 9 ){
				targetiD = 2;
				
			}else if( target == 9 ){
				targetiD = 3;
			}
			
		}else if( size == 3 ) {
			
			int target = random.nextInt( 10 );
			
			if( target < 5 ) {
				targetiD = 0;
				
			}else if( target < 9 ){
				targetiD = 1;
					
			}else if( target == 9 ){
				targetiD = 2;
			}
			
		}else if( size == 2 ) {
			
			int target = random.nextInt( 2 );
			
			if( target == 0 ) {
				targetiD = 0;
				
			}else{
				targetiD = 1;
			}
			
		}else{
			targetiD = 0;
		}
		
		return targetiD;
	}

}
