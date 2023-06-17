package points;

import java.util.Random;

public class CurrentPlayerOffensivePower implements Points{

	private final int SPECIFICATION_MIN_OP = 0;
	private final int SPECIFICATION_MAX_OP = 999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	private final String toStringMessage = "の攻撃!!!";
	
	private final Integer CURRENT_OP;
	
	
	CurrentPlayerOffensivePower( final Integer currentOP ){
		
		if( currentOP > SPECIFICATION_MAX_OP || currentOP < SPECIFICATION_MIN_OP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		this.CURRENT_OP = currentOP;

	}
	
	
	//キャラクターの攻撃力を増やす。
	public CurrentPlayerOffensivePower increasePlayerOffensivePower( final CurrentPlayerOffensivePower increaseOffensivePower ) {
		
		if( increaseOffensivePower.CURRENT_OP > SPECIFICATION_MAX_OP || increaseOffensivePower.CURRENT_OP < SPECIFICATION_MIN_OP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		final int addPoint = this.CURRENT_OP + increaseOffensivePower.CURRENT_OP;
		
		return new CurrentPlayerOffensivePower( addPoint );
		
	}
	
	//キャラクターの攻撃力を減らす
	public CurrentPlayerOffensivePower decreasePlayerOffensivePower( final CurrentPlayerOffensivePower decreaseOffensivePower ) {
		
		if( decreaseOffensivePower.CURRENT_OP > SPECIFICATION_MAX_OP || decreaseOffensivePower.CURRENT_OP < SPECIFICATION_MIN_OP ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		if( this.CURRENT_OP < decreaseOffensivePower.CURRENT_OP ) {
			
			return new CurrentPlayerOffensivePower( SPECIFICATION_MIN_OP );
		}
		
		final int lowerPoint = this.CURRENT_OP - decreaseOffensivePower.CURRENT_OP;
		
		return new CurrentPlayerOffensivePower( lowerPoint );
		
	}
	
	public PlayerCharacter attack( final PlayerCharacter targetCharacter ) {
		
		targetCharacter.damage( this.randomNumberCreate() );
		
		return targetCharacter;
	}
	
	private Integer randomNumberCreate() {
		final int random = new Random().nextInt( 10 );
		
		return CURRENT_OP + random ;
	}
	
	public String toString() {
		return toStringMessage;
	}
	
}
