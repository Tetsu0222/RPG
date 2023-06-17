package points;

public class EnemyCharacter implements Characters{

	private final Integer enemyCharacterId;
	private final MaxEnemyHitPoints MAX_HP;
	private final String name;
	
	private CurrentEnemyHitPoints HP;
	private CurrentEnemyOffensivePower ATK;
	private CurrentEnemyDefensePower DEF;
	
	
	EnemyCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def ){
		
		this.enemyCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxEnemyHitPoints( initialHP );
		this.HP = new CurrentEnemyHitPoints( initialHP );
		this.ATK = new CurrentEnemyOffensivePower( atk );
		this.DEF = new CurrentEnemyDefensePower( def );

	}
	
	public void healing( final CurrentEnemyHitPoints healingPoint ) {
		this.HP = HP.increaseEnemyHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final Integer damagePoint ) {
		final CurrentEnemyHitPoints resultDamage = DEF.defense( damagePoint );
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
		return enemyCharacterId.hashCode();
	}
	
	public boolean equals( Object obj ) {
		return this.hashCode() == obj.hashCode();
	}
	
	public String toString() {
		return name;
	}
	
}