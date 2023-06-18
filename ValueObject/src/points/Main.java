package points;

import java.util.ArrayList;
import java.util.List;

public class Main {

	
	public static void main(String[] args) {
		
		
		List< PlayerCharacter > playerCharacterList = new ArrayList<>();
		List< EnemyCharacter > enemyCharacterList = new ArrayList<>();
		
		PlayerCharacter testA = new PlayerCharacter( 1 , "テストA" , 100 , 30 , 10 );
		PlayerCharacter testD = new PlayerCharacter( 2 , "テストD" , 100 , 30 , 10 );
		EnemyCharacter testB = new EnemyCharacter( 4 , "テストB" , 100 , 30 , 10 );
		EnemyCharacter testC = new EnemyCharacter( 5 , "テストC" , 100 , 30 , 10 );
		
		playerCharacterList.add( testA );
		playerCharacterList.add( testD );
		
		enemyCharacterList.add( testB );
		enemyCharacterList.add( testC );
		
		BattleTest battle = new BattleTest( playerCharacterList , enemyCharacterList );
		battle.battleStart();
	}
}



/*

Map< Integer , PlayerCharacter > playerCharacterMap = new HashMap<>();
playerCharacterMap.put( 1 , testA );
playerCharacterMap.put( 2 , testB );
		
Map< Integer , EnemyCharacter > enemyCharacterMap = new HashMap<>();
enemyCharacterMap.put( 1 , testC );
		
List< PlayerCharacter > playerCharacterList = new ArrayList<>();
playerCharacterList.add( testA );
playerCharacterList.add( testB );
		
List< EnemyCharacter > enemyCharacterList = new ArrayList<>();
enemyCharacterList.add( testC );

Set< PlayerCharacter > playerCharacterSet = new HashSet<>();
playerCharacterSet.add( testA );
playerCharacterSet.add( testB );

Set< EnemyCharacter > enemyCharacterSet = new HashSet<>();
enemyCharacterSet.add( testC );
*/


