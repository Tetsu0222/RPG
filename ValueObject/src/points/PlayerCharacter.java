package points;

public class PlayerCharacter implements Characters{

	private final Integer playerCharacterId;
	private final MaxPlayerHitPoints MAX_HP;
	private final String name;
	
	private CurrentPlayerHitPoints HP;
	private CurrentPlayerOffensivePower ATK;
	private CurrentPlayerDefensePower DEF;
	private boolean survivalFlag;
	
	
	PlayerCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def ){
		
		this.playerCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxPlayerHitPoints( initialHP );
		this.HP = new CurrentPlayerHitPoints( initialHP );
		this.ATK = new CurrentPlayerOffensivePower( atk );
		this.DEF = new CurrentPlayerDefensePower( def );
		this.survivalFlag = true;
	}
	
	public void healing( final CurrentPlayerHitPoints healingPoint ) {
		this.HP = HP.increasePlayerHitPoints( healingPoint , MAX_HP );
	}
	
	public void damage( final Integer damagePoint ) {
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return ;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
		final CurrentPlayerHitPoints resultDamage = DEF.defense( damagePoint );
		this.HP = HP.decreasePlayerHitPoints( resultDamage );
		
		if( HP.is_Dead() ) {
			this.survivalFlag = false;
		}
		
		this.displayAction( resultDamage );
		this.currentHP();
	}
	
	public EnemyCharacter attak( final EnemyCharacter targetCharacter ) {
		
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
		return playerCharacterId.hashCode();
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