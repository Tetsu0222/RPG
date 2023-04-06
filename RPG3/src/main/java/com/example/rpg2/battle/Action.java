package com.example.rpg2.battle;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.Status;

import lombok.Data;

@Data
public class Action {
	
	private Integer recovery;
	private Integer damage  ;
	private String  damageMessage;
	private String  recoveryMessage;
	private String  buffMessage;
	
	Random random = new Random();
	
	
	//味方への蘇生魔法
	public AllyData actionResuscitationMagic( AllyData allyData , AllyData receptionAllyData , Magic magic ) {
		
		if( magic.getPercentage() == 1 ) {
			int HP = receptionAllyData.getMaxHP();
			receptionAllyData.setCurrentHp( HP );
			receptionAllyData.resuscitation();
			this.recoveryMessage = "完全に生き返った!!";
			
		}else{
			
			//蘇生判定処理
			int judgement = random.nextInt( 3 );
			if( judgement > 0 ) {
				int HP = receptionAllyData.getMaxHP() / 2;
				receptionAllyData.setCurrentHp( HP );
				receptionAllyData.resuscitation();
				this.recoveryMessage = "生き返った!!";
			}else{
				this.recoveryMessage = "生き返らなかった･･･";
			}
		}
		
		return receptionAllyData;
		
	}
	
	
	//妨害魔法
	public MonsterData debuffMagicMagic( AllyData allyData , MonsterData monsterData , Magic magic) {
		
		//防御妨害魔法の処理
		if( magic.getBuffcategory().equals( "DEF" )) {
			
			double def = monsterData.getCurrentDEF();
			
			//下限チェック
			if( def < 1 ) {
				this.buffMessage = "守備力は、これ以上は下がらなかった･･･";
			
			//下限未達
			}else{
				double buffPoint = magic.getPercentage() + 1.2 ;
				def = def / buffPoint;
				
				//補正値が上限を上回らないように再分岐
				if( def < 1 ) {
					def = 0 ;
				}
				monsterData.setCurrentDEF( (int) def );
				this.buffMessage = "守備力が下がった!!";
			}
		}
		
		return monsterData;
	}
	

	//MP消費処理
	public AllyData consumptionMp( AllyData allyData , Magic magic ) {
		
		int MP = allyData.getCurrentMp();
		MP -= magic.getMp();
		allyData.setCurrentMp( MP );
		
		return allyData;
	}
	
	
	//死亡時の処理
	public void noAction() {
		//明示的に何も処理しない。
	}
	
	//正常からへバフ状態（期限付き）へ変化
	public Set<Status> goodStatus( AllyData receptionAllyData ){
		
		Set<Status> statusSet = receptionAllyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		return statusSet;
	}
	

}
