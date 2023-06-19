package points;

import java.util.Random;

public class PlayerCharacter implements Characters{

	private final Integer playerCharacterId;
	private final MaxPlayerHitPoints MAX_HP;
	private final String name;
	private final Random random = new Random();
	
	private CurrentPlayerHitPoints HP;
	private CurrentPlayerOffensivePower ATK;
	private CurrentPlayerDefensePower DEF;
	private Integer SPD;
	private boolean survivalFlag;
	
	
	//nullの可能性あり。
	private EnemyCharacter targetEnemyCharacter;
	
	
	PlayerCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def , final Integer spd ){
		
		this.playerCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxPlayerHitPoints( initialHP );
		this.HP = new CurrentPlayerHitPoints( initialHP );
		this.ATK = new CurrentPlayerOffensivePower( atk );
		this.DEF = new CurrentPlayerDefensePower( def );
		this.SPD = spd;
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
	
	public EnemyCharacter attak() {
		
		
		if( this.targetEnemyCharacter == null ) {
			throw new IllegalStateException( "対象が選択されていません。" );
		}
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return targetEnemyCharacter;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
		this.displayAction( ATK );
		return ATK.attack( targetEnemyCharacter );
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
	
	public void targetEnemyCharacterSelection( final EnemyCharacter targetCharacter ) {
		this.targetEnemyCharacter = targetCharacter;
	}
	
	public Integer getSPD() {
		final Integer spe = random.nextInt( this.SPD / 2 + 1 );
		return this.SPD + spe;
	}
	
	
}