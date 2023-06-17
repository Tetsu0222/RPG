package points;

public class CurrentEnemyDefensePower implements Points{
	
	private final int SPECIFICATION_MIN_DEF = 0;
	private final int SPECIFICATION_MAX_DEF = 999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	
	private final Integer CURRENT_DEF;
	
	
	CurrentEnemyDefensePower( final Integer def ){
		
		if( def > SPECIFICATION_MAX_DEF || def < SPECIFICATION_MIN_DEF ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		this.CURRENT_DEF = def;

	}
	
	//キャラクターの防御力を増やす。
	public CurrentEnemyDefensePower increasePlayerOffensivePower( final CurrentEnemyDefensePower increaseDefensePower) {
		
		if( increaseDefensePower.CURRENT_DEF > SPECIFICATION_MAX_DEF || increaseDefensePower.CURRENT_DEF < SPECIFICATION_MIN_DEF ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		final int addPoint = this.CURRENT_DEF + increaseDefensePower.CURRENT_DEF;
		
		return new CurrentEnemyDefensePower( addPoint );
		
	}
	
	//キャラクターの防御力を減らす。
	public CurrentEnemyDefensePower decreasePlayerOffensivePower( final CurrentEnemyDefensePower decreaseDefensePower ) {
		
		if( decreaseDefensePower.CURRENT_DEF > SPECIFICATION_MAX_DEF || decreaseDefensePower.CURRENT_DEF < SPECIFICATION_MIN_DEF ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		if( this.CURRENT_DEF < decreaseDefensePower.CURRENT_DEF ) {
			
			return new CurrentEnemyDefensePower( SPECIFICATION_MIN_DEF );
		}
		
		final int lowerPoint = this.CURRENT_DEF - decreaseDefensePower.CURRENT_DEF;
		
		return new CurrentEnemyDefensePower( lowerPoint );
		
	}
	
	public CurrentEnemyHitPoints defense( final Integer damagePoint ) {
		
		final Integer resultDamage = damagePoint < CURRENT_DEF / 2  ? SPECIFICATION_MIN_DEF : damagePoint - CURRENT_DEF / 2  ;
		
		return new CurrentEnemyHitPoints( resultDamage );
		
	}
	
	

}
