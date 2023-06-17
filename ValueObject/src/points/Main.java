package points;

public class Main {

	public static void main(String[] args) {
		
		PlayerCharacter testA = new PlayerCharacter( 1 , "テストA" , 100 , 30 , 10 );
		EnemyCharacter testB = new EnemyCharacter( 1 , "テストB" , 100 , 30 , 10 );
		
		
		while( testA.is_Survival() && testB.is_Survival() ) {
			testA.attak( testB );
			testB.attak( testA );
		}

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


