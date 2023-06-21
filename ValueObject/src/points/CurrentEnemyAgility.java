package points;

import java.util.Random;

public class CurrentEnemyAgility implements Points{
	
	private final int SPECIFICATION_MIN_spe = 0;
	private final int SPECIFICATION_MAX_spe = 999;
	private final String IllegalArgumentException_MESSAGE = "不正な値です。";
	
	private final Integer CURRENT_spe;
	
	
	CurrentEnemyAgility( final Integer spe ){
		
		if( spe > SPECIFICATION_MAX_spe || spe < SPECIFICATION_MIN_spe ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		this.CURRENT_spe = spe;

	}
	
	//キャラクターの防御力を増やす。
	public CurrentEnemyAgility increasePlayerOffensivePower( final CurrentEnemyAgility increasespeensePower) {
		
		if( increasespeensePower.CURRENT_spe > SPECIFICATION_MAX_spe || increasespeensePower.CURRENT_spe < SPECIFICATION_MIN_spe ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		final int addPoint = this.CURRENT_spe + increasespeensePower.CURRENT_spe;
		
		return new CurrentEnemyAgility( addPoint );
		
	}
	
	//キャラクターの防御力を減らす。
	public CurrentEnemyAgility decreasePlayerOffensivePower( final CurrentEnemyAgility decreasespeensePower ) {
		
		if( decreasespeensePower.CURRENT_spe > SPECIFICATION_MAX_spe || decreasespeensePower.CURRENT_spe < SPECIFICATION_MIN_spe ) {
			throw new IllegalArgumentException( IllegalArgumentException_MESSAGE );
		}
		
		if( this.CURRENT_spe < decreasespeensePower.CURRENT_spe ) {
			
			return new CurrentEnemyAgility( SPECIFICATION_MIN_spe );
		}
		
		final int lowerPoint = this.CURRENT_spe - decreasespeensePower.CURRENT_spe;
		
		return new CurrentEnemyAgility( lowerPoint );
		
	}
	
	public Integer getTurnSPD( Random random ) {
		final Integer spe = this.CURRENT_spe + random.nextInt( this.CURRENT_spe / 2 + 1 );
		return spe;
	}
	
	

}
