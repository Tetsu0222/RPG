package points;

public class Main {

	public static void main(String[] args) {
		
		
		PlayerCharacter test = new PlayerCharacter( 20 , 40 );
		
		test.damage( new CurrentPlayerHitPoints( 10 ));
		test.healing( new CurrentPlayerHitPoints( 50 ));
		
		test.display();

	}

}
