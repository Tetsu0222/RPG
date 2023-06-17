package points;

public class PlayerCharacter implements Characters{

	private final Integer playerCharacterId;
	private final MaxPlayerHitPoints MAX_HP;
	private final String name;
	
	private CurrentPlayerHitPoints HP;
	private CurrentPlayerOffensivePower ATK;
	private CurrentPlayerDefensePower DEF;
	
	
	PlayerCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def ){
		
		this.playerCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxPlayerHitPoints( initialHP );
		this.HP = new CurrentPlayerHitPoints( initialHP );
		this.ATK = new CurrentPlayerOffensivePower( atk );
		this.DEF = new CurrentPlayerDefensePower( def );

	}
	
	public void healing( final CurrentPlayerHitPoints healingPoint ) {
		this.HP = HP.increasePlayerHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final Integer damagePoint ) {
		final CurrentPlayerHitPoints resultDamage = DEF.defense( damagePoint );
		this.HP = HP.decreasePlayerHitPoints( resultDamage );
		this.displayAction( resultDamage );
	}
	
	public PlayerCharacter attak( final PlayerCharacter targetCharacter ) {
		this.displayAction( ATK );
		return ATK.attack( targetCharacter );
	}
	
	public void displayAction( Points command ) {
		final StringBuilder message = new StringBuilder( name );
		message.append( command );
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