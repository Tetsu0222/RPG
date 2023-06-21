package points;

import java.util.HashMap;
import java.util.Map;

public class Main {

	
	public static void main(String[] args) {
		
		Map< Integer , PlayerCharacter > playerCharacterMap = new HashMap<>();
		Map< Integer , EnemyCharacter > enemyCharacterMap = new HashMap<>();
		
		PlayerCharacter testA = new PlayerCharacter( 0 , "テストA" , 100 , 30 , 10 , 10 );
		PlayerCharacter testD = new PlayerCharacter( 1 , "テストD" , 100 , 30 , 10 , 40 );
		PlayerCharacter testE = new PlayerCharacter( 2 , "テストE" , 100 , 30 , 10 , 15 );
		PlayerCharacter testF = new PlayerCharacter( 3 , "テストF" , 100 , 30 , 10 , 15 );
		EnemyCharacter testB = new EnemyCharacter( 4 , "テストB" , 700 , 20 , 10 , 5 );
		EnemyCharacter testC = new EnemyCharacter( 5 , "テストC" , 100 , 30 , 10 , 10);
		
		playerCharacterMap.put( 0 , testA );
		playerCharacterMap.put( 1 , testD );
		playerCharacterMap.put( 2 , testE );
		playerCharacterMap.put( 3 , testF );
		
		enemyCharacterMap.put( 4 , testB );
		enemyCharacterMap.put( 5 , testC );
		
		BattleTestMap battle = new BattleTestMap( playerCharacterMap , enemyCharacterMap );
		battle.battleStart();
	}
}
