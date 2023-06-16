package points;

public class PlayerCharacter implements Characters{

	private CurrentPlayerHitPoints HP;
	private final MaxPlayerHitPoints MAX_HP;
	
	
	PlayerCharacter( final Integer HP , final Integer maxHP ){
		this.HP = new CurrentPlayerHitPoints( HP );
		this.MAX_HP = new MaxPlayerHitPoints( maxHP );
		
	}
	
	public void healing( final CurrentPlayerHitPoints healingPoint ) {
		this.HP = HP.increasePlayerHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final CurrentPlayerHitPoints damagePoint ) {
		this.HP = HP.decreasePlayerHitPoints( damagePoint );
	}
	
	public void display() {
		System.out.println( HP );
	}
	
}