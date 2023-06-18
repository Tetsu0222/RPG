package points;

import java.util.HashMap;
import java.util.Map;

public class Main {

	
	public static void main(String[] args) {
		
		Map< Integer , PlayerCharacter > playerCharacterMap = new HashMap<>();
		Map< Integer , EnemyCharacter > enemyCharacterMap = new HashMap<>();
		
		PlayerCharacter testA = new PlayerCharacter( 1 , "テストA" , 100 , 30 , 10 );
		PlayerCharacter testD = new PlayerCharacter( 2 , "テストD" , 100 , 30 , 10 );
		PlayerCharacter testE = new PlayerCharacter( 3 , "テストE" , 100 , 30 , 10 );
		EnemyCharacter testB = new EnemyCharacter( 4 , "テストB" , 700 , 20 , 10 );
		//EnemyCharacter testC = new EnemyCharacter( 5 , "テストC" , 100 , 30 , 10 );
		
		playerCharacterMap.put( 1 , testA );
		playerCharacterMap.put( 2 , testD );
		playerCharacterMap.put( 3 , testE );
		
		enemyCharacterMap.put( 4 , testB );
		//enemyCharacterMap.put( 5 , testC );
		
		BattleTestMap battle = new BattleTestMap( playerCharacterMap , enemyCharacterMap );
		battle.battleStart();
	}
}
