package points;

public class Main {

	public static void main(String[] args) {
		
		
		PlayerCharacter testA = new PlayerCharacter( 1 , "テストA" , 20 , 30 , 10 );
		PlayerCharacter testB = new PlayerCharacter( 2 , "テストB" , 100 , 15 , 10 );
		
		testA.attak( testB );
		testB.attak( testA );
		
	}

}
