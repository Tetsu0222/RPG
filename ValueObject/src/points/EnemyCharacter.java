package points;

import java.util.List;
import java.util.Random;

public class EnemyCharacter implements Characters{

	//コンストラクタで初期化する定数
	private final Integer enemyCharacterId;
	private final MaxEnemyHitPoints MAX_HP;
	private final String name;
	private final Random random = new Random();
	
	//変数
	private CurrentEnemyHitPoints HP;
	private CurrentEnemyOffensivePower ATK;
	private CurrentEnemyDefensePower DEF;
	private CurrentEnemyAgility SPD;
	private boolean survivalFlag;
	
	//nullの可能性あり。
	private PlayerCharacter targetPlayerCharacter;
	private List< PlayerCharacter > playerList;
	
	EnemyCharacter( final Integer id , final String name , final Integer initialHP , final Integer atk , final Integer def , final Integer spd ){
		
		this.enemyCharacterId = id;
		this.name = name + id;
		this.MAX_HP = new MaxEnemyHitPoints( initialHP );
		this.HP = new CurrentEnemyHitPoints( initialHP );
		this.ATK = new CurrentEnemyOffensivePower( atk );
		this.DEF = new CurrentEnemyDefensePower( def );
		this.SPD = new CurrentEnemyAgility( spd );
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
	
	private PlayerCharacter attak() {
		
		if( this.targetPlayerCharacter == null ) {
			throw new IllegalStateException( "対象が選択されていません。" );
		}
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return targetPlayerCharacter;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
		this.displayAction( ATK );
		return ATK.attack( targetPlayerCharacter );
	}
	
	private PlayerCharacter attakAll() {
		
		//戦闘不能ならば呼び出されないように修正する予定
		if( !survivalFlag ) {
			return targetPlayerCharacter;
		}
		
		if( HP.is_Dead() ) {
			throw new IllegalStateException( "キャラクターは戦闘不能なのに呼び出されています" );
		}
		
		for( int i = 0 ; this.playerList.size() > i ; i++ ) {
			this.targetPlayerCharacter = playerList.get( i );
			this.attak();
		}
		
		this.displayAction( ATK );
		return ATK.attack( targetPlayerCharacter );
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
	
	public void targetSelection( final List< PlayerCharacter > playerList ) {
		this.playerList = playerList;
		this.targetPlayerCharacter = playerList.get( random.nextInt( playerList.size() ));
		
		final int actionPattern = random.nextInt( 3 );
		
		switch( actionPattern ) {
		
			case 0:
			case 1:
				this.attak();
			break;
			
			case 2:
				this.attakAll();
			break;	
		}
	}
	
	public Integer getSPD() {
		
		return this.SPD.getTurnSPD( random );
	}
	
}