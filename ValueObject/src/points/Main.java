package points;

import java.util.HashMap;
import java.util.Map;

public class Main {

	public static void main(String[] args) {
		
		
		PlayerCharacter testA = new PlayerCharacter( 1 , "テストA" , 20 );
		PlayerCharacter testB = new PlayerCharacter( 2 , "テストA" , 100 );
		
		Map< String , PlayerCharacter > playerCharacterMap = new HashMap<>();
		playerCharacterMap.put( testA.toString() , testA );
		playerCharacterMap.put( testB.toString() , testB );
		
		testB.damage( new CurrentPlayerHitPoints( 30 ));
		testB.display();
		
		System.out.println( testA.equals( testB ));
	}

}
