package points;

public class CurrentEnemyHitPoints implements Points{

	private final int SPECIFICATION_MIN_HP = 0;
	private final int SPECIFICATION_MAX_HP = 99999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	private final String toStringMessage = "のダメージを受けた!!!";
	
	private final Integer CURRENT_HP;

	
	//キャラクターのHPオブジェクトを生成
	CurrentEnemyHitPoints( final Integer currentHP ){
		
		if( currentHP > SPECIFICATION_MAX_HP || currentHP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		this.CURRENT_HP = currentHP;
		
	}
	
	
	//キャラクターのHPを増やす（回復）
	public CurrentEnemyHitPoints increaseEnemyHitPoints( final CurrentEnemyHitPoints increaseHitPoints , final MaxEnemyHitPoints maxHitPoints ) {
		
		if( increaseHitPoints.CURRENT_HP > SPECIFICATION_MAX_HP || increaseHitPoints.CURRENT_HP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		final int addPoint = this.CURRENT_HP + increaseHitPoints.CURRENT_HP;
		
		if( maxHitPoints.excessMAX_HP( addPoint )) {
			return maxHitPoints.makeEqualHP();
		}
		
		return new CurrentEnemyHitPoints( addPoint );
		
	}
	
	
	//キャラクターのHPを減らす
	public CurrentEnemyHitPoints decreasePlayerHitPoints( final CurrentEnemyHitPoints decreaseHitPoints ) {
		
		if( decreaseHitPoints.CURRENT_HP > SPECIFICATION_MAX_HP || decreaseHitPoints.CURRENT_HP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		if( this.CURRENT_HP < decreaseHitPoints.CURRENT_HP ) {
			
			return new CurrentEnemyHitPoints( SPECIFICATION_MIN_HP );
		}
		
		final int lowerPoint = this.CURRENT_HP - decreaseHitPoints.CURRENT_HP;
		
		return new CurrentEnemyHitPoints( lowerPoint );
		
	}

	
	public String toString() {
		return "は" + CURRENT_HP + toStringMessage;
	
	}
	
	

}
