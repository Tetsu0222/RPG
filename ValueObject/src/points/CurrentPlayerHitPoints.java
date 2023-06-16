package points;

public class CurrentPlayerHitPoints implements Points{

	private final int SPECIFICATION_MIN_HP = 0;
	private final int SPECIFICATION_MAX_HP = 999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	private final String toStringMessage = "現在のHPは";
	
	private final Integer CURRENT_HP;

	
	//キャラクターのHPオブジェクトを生成
	CurrentPlayerHitPoints( final Integer currentHP ){
		
		if( currentHP > SPECIFICATION_MAX_HP || currentHP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		this.CURRENT_HP = currentHP;
		
	}
	
	
	//キャラクターのHPを増やす（回復）
	public CurrentPlayerHitPoints increasePlayerHitPoints( final CurrentPlayerHitPoints increaseHitPoints , final MaxPlayerHitPoints maxHitPoints ) {
		
		if( increaseHitPoints.CURRENT_HP > SPECIFICATION_MAX_HP || increaseHitPoints.CURRENT_HP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		final int addPoint = this.CURRENT_HP + increaseHitPoints.CURRENT_HP;
		
		if( maxHitPoints.excessMAX_HP( addPoint )) {
			return maxHitPoints.makeEqualHP();
		}
		
		return new CurrentPlayerHitPoints( addPoint );
		
	}
	
	
	//キャラクターのHPを減らす
	public CurrentPlayerHitPoints decreasePlayerHitPoints( final CurrentPlayerHitPoints decreaseHitPoints ) {
		
		if( decreaseHitPoints.CURRENT_HP > SPECIFICATION_MAX_HP || decreaseHitPoints.CURRENT_HP < SPECIFICATION_MIN_HP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		if( this.CURRENT_HP < decreaseHitPoints.CURRENT_HP ) {
			
			return new CurrentPlayerHitPoints( SPECIFICATION_MIN_HP );
		}
		
		final int lowerPoint = this.CURRENT_HP - decreaseHitPoints.CURRENT_HP;
		
		return new CurrentPlayerHitPoints( lowerPoint );
		
	}

	
	public String toString() {
		return toStringMessage + CURRENT_HP;
	
	}
	
	

}
