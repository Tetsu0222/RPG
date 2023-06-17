package points;

public class EnemyCharacter implements Characters{

	private final Integer enemyCharacterId;
	private final MaxEnemyHitPoints MAX_HP;
	private final String name;
	
	private CurrentEnemyHitPoints HP;
	private CurrentEnemyOffensivePower ATK;
	private CurrentEnemyDefensePower DEF;
	private boolean survivalFlag;
	
	EnemyCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def ){
		
		this.enemyCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxEnemyHitPoints( initialHP );
		this.HP = new CurrentEnemyHitPoints( initialHP );
		this.ATK = new CurrentEnemyOffensivePower( atk );
		this.DEF = new CurrentEnemyDefensePower( def );
		this.survivalFlag = true;
	}
	
	public void healing( final CurrentEnemyHitPoints healingPoint ) {
		this.HP = HP.increaseEnemyHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final Integer damagePoint ) {
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return ;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
		final CurrentEnemyHitPoints resultDamage = DEF.defense( damagePoint );
		
		this.HP = HP.decreasePlayerHitPoints( resultDamage );
		
		if( HP.is_Dead() ) {
			this.survivalFlag = false;
		}
		
		this.displayAction( resultDamage );
		this.currentHP();
	}
	
	public PlayerCharacter attak( final PlayerCharacter targetCharacter ) {
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return targetCharacter;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
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
	
	
	public boolean is_Survival() {
		return this.survivalFlag;
	}
	
	public void currentHP() {
		System.out.println( HP.toString( "現在のHPは" ));
	}
	
}