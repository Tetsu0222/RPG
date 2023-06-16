package points;

public class PlayerCharacter implements Characters{

	private final Integer playerCharacterId;
	private final MaxPlayerHitPoints MAX_HP;
	private final String name;
	
	private CurrentPlayerHitPoints HP;
	
	
	PlayerCharacter( final Integer id , final String name , final Integer initialHP ){
		
		this.playerCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxPlayerHitPoints( initialHP );
		this.HP = new CurrentPlayerHitPoints( initialHP );

	}
	
	public void healing( final CurrentPlayerHitPoints healingPoint ) {
		this.HP = HP.increasePlayerHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final CurrentPlayerHitPoints damagePoint ) {
		this.HP = HP.decreasePlayerHitPoints( damagePoint );
	}
	
	public void display() {
		final StringBuilder message = new StringBuilder( name );
		message.append( "の" );
		message.append( HP );
		System.out.println( message );
	}
	
	public int hashCode() {
		return playerCharacterId.hashCode();
	}
	
	public boolean equals( Object obj ) {
		return this.hashCode() == obj.hashCode();
	}
	
	public String toString() {
		return name;
	}
	
}