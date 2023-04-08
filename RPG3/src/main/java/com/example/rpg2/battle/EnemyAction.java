package com.example.rpg2.battle;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.example.rpg2.entity.MonsterPattern;
import com.example.rpg2.process.Awakening;
import com.example.rpg2.process.BadStatusAlly;
import com.example.rpg2.process.Funeral;

import lombok.Data;

@Data
public class EnemyAction {
	
	private String pattern;
	private String range;
	private MonsterPattern monsterPattern;
	private MonsterData monsterData;
	private Integer targetId;

	private Integer recovery;
	private Integer damage  = 0;
	private String  startMessage ;
	private String  battleMessage;
	private String  buffMessage;
	private String  resultMessage;
	private String  dedMessage;
	
	Random random = new Random();
	
	public void decision( MonsterData monsterData ) {
		this.monsterData = monsterData;
		
		//モンスターの行動をリストからランダムで指定
		this.monsterPattern = monsterData.getPatternList().get( random.nextInt( monsterData.getPatternList().size() ) );
		this.pattern = monsterPattern.getCategory();
		this.range   = monsterPattern.getRange();
		this.startMessage = monsterData.getName() + monsterPattern.getText();
	}
	
	
	//単体攻撃を処理
	public AllyData attackSkillSingle( Map<Integer,AllyData> partyMap , List<Integer> targetList ) {
		
		//攻撃する相手を乱数で決定
		Integer target = random.nextInt( targetList.size() );
		this.targetId  = targetList.get( target );
		AllyData allyData = partyMap.get( targetId );
		
		//悪性ステータス異常
		if( !monsterPattern.getBuffcategory().equals( "no" ) ) {
			BadStatusAlly badStatusAlly = new BadStatusAlly();
			allyData = badStatusAlly.bad( allyData, monsterPattern );
			this.buffMessage = badStatusAlly.getBuffMessage();
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" ) && monsterPattern.getPercentage() == 0 ) {
			allyData = this.physical( allyData );
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			allyData = this.magic( allyData );
		}

		return allyData;
	}
	
	
	//全体攻撃を処理
	public AllyData attackSkillWhole( Map<Integer,AllyData> partyMap , Integer target ) {
		
		this.targetId = target;
		AllyData allyData = partyMap.get( targetId );
		
		//悪性ステータス異常
		if( !monsterPattern.getBuffcategory().equals( "no" ) ) {
			BadStatusAlly badStatusAlly = new BadStatusAlly();
			allyData = badStatusAlly.bad( allyData, monsterPattern );
			this.buffMessage = badStatusAlly.getBuffMessage();
		}
		
		//物理攻撃の計算処理
		if( this.pattern.equals( "attackskill" ) && monsterPattern.getPercentage() == 0 ) {
			allyData = this.physical( allyData );
			
		//魔法攻撃の計算処理
		}else if( this.pattern.equals( "attackmagic" ) && monsterPattern.getPercentage() == 0 ){
			allyData = this.magic( allyData );
		}
		
		return allyData;
	}
	
	
	//死亡時などの処理
	public void noAction() {
		//明示的に何も処理しない。
	}
	
	
	//物理攻撃を処理
	public AllyData physical( AllyData allyData ) {
		
		Integer plusDamage = 0;
		
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
			
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() + 1 ) / 8
							+ random.nextInt( monsterData.getCurrentATK() ) / 8;
		}
		
		//(攻撃力-防御力/2) + 乱数 = ダメージ
		this.damage = ( monsterData.getCurrentATK() - ( allyData.getCurrentDEF() / 2 )) + plusDamage;
		
		if( this.isDefense( allyData )){
			this.damage = damage / 2;
		}
		
		if( damage < 0 ) {
			this.damage = 0;
			this.battleMessage = allyData.getName() + "にダメージを与えられない…";
			
		}else{
			this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
			
			//対象が睡眠状態の場合は、それを解除する。
			if( allyData.getStatusSet().stream()
					.filter( s -> s.getName().equals( "睡眠" ))
					.count() == 1 ) {
				this.resultMessage = allyData.getName() + "は目を覚ました!";
				allyData = Awakening.awakening( allyData );
			}
		}
		
		//ダメージ計算
		Integer HP = allyData.getCurrentHp() - damage;

		//攻撃で味方がやられてしまった時の処理
		if( HP <= 0 ) {
			allyData = Funeral.execution( allyData );
			this.dedMessage = allyData.getName() + "は死んでしまった…";
		
		//ダメージを反映
		}else{
			allyData.setCurrentHp( HP );
		}
		
		return allyData;
	}
	
	
	//魔法攻撃を処理
	public AllyData magic( AllyData allyData ) {
		
		Integer plusDamage = 0;
		
		if( monsterPattern.getPoint() == 0 ){
			plusDamage = random.nextInt( monsterData.getCurrentATK() + 1 ) / 4;
			
		}else{
			plusDamage = random.nextInt( monsterPattern.getPoint() + 1 ) / 8
							+ random.nextInt( monsterData.getCurrentATK() ) / 8;
		}
		
		//攻撃力 + 乱数 = ダメージ(防御力無視だけで暫定対応、耐性値を実装して値に干渉する予定)
		this.damage = monsterData.getCurrentATK() + plusDamage;
		
		if( this.isDefense( allyData )){
			this.damage = damage / 2;
		}
		
		if( damage < 0 ) {
			this.damage = 0;
			this.battleMessage = allyData.getName() + "にダメージを与えられない…";
		}else{
			this.battleMessage = allyData.getName() + "に" + damage + "のダメージ!!!";
		}
		
		//ダメージ計算
		Integer HP = allyData.getCurrentHp() - damage;

		//攻撃で味方がやられてしまった時の処理
		if( HP <= 0 ) {
			allyData = Funeral.execution( allyData );
			this.dedMessage = allyData.getName() + "は死んでしまった…";
		
		//ダメージを反映
		}else{
			allyData.setCurrentHp( HP );
		}
		
		return allyData;
	}
	
	
	//防御の有無チェック
	public boolean isDefense( AllyData allyData ) {
		
		//対象者のステータス異常の中に防御が含まれているか確認(1が返れば含まれていると判定)
		Long i = allyData.getStatusSet()
				.stream()
				.filter( s -> s.getName().equals( "防御" ) )
				.count();
		
		//防御が含まれていればtureを返す。
		return i == 1;
	}
	
}
