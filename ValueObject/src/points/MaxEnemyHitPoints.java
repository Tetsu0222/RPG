package points;

public class MaxEnemyHitPoints {
	
	
	private final int SPECIFICATION_MIN_HP = 0;
	private final int SPECIFICATION_MAX_HP = 99999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	private final String toStringMessage = "最大のHPは";
	
	private final Integer MAX_HP;
	
	
	MaxEnemyHitPoints( final Integer maxHP ){
		
		if( maxHP > SPECIFICATION_MAX_HP || maxHP < SPECIFICATION_MIN_HP ) {
			
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
			
		}
		
		this.MAX_HP = maxHP;
	}
	
	
	public boolean excessMAX_HP( final Integer increaseHitPoints ) {
		
		return increaseHitPoints >= this.MAX_HP;
		
	}
	
	
	public CurrentEnemyHitPoints makeEqualHP() {
		
		return new CurrentEnemyHitPoints( this.MAX_HP );
		
	}
	
	
	public String toString() {
		return toStringMessage + MAX_HP;
	
	}
	

}
