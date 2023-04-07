package com.example.rpg2.action;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;

import lombok.Data;


@Data
public class RecoveryMagic implements TargetAllyAction {
	
	private Integer recovery;
	private String  stratMessage;
	private String  notEnoughMpMessage;
	private String  recoveryMessage;
	private AllyData allyData;
	private Magic    magic;
	private String resultMessage;
	
	
	//コンストラクタ 必要な情報を設定
	public RecoveryMagic( AllyData allyData , Magic magic ) {
		this.allyData = allyData;
		this.magic = magic;
		this.stratMessage =  allyData.getName() + "は" + magic.getName() + "を放った!!";
	}
	
	//MP判定
	public boolean isNotEnoughMp() {
		boolean check = magic.getMp() <= allyData.getCurrentMp();
		
		if( !check ) {
			this.notEnoughMpMessage = "しかしMPが足りない･･･" ;
		}
		
		return !check;
	}
	
	
	
	//味方への回復魔法
	public AllyData action( AllyData receptionAllyData ) {
		
		String buffCategory = magic.getBuffcategory();
		String category     = magic.getCategory();
		
		//通常の回復魔法
		if( buffCategory.equals( "no" ) && category.equals( "recoverymagic" ) ) {
		
			Random random = new Random();
			
			//回復量変動タイプ
			if( magic.getPercentage() == 0 ) {
				int HP = receptionAllyData.getCurrentHp();
				this.recovery = magic.getPoint() + random.nextInt( magic.getPoint() / 4 ) - random.nextInt( magic.getPoint() / 4 );
					
				HP += recovery;
					
				if( receptionAllyData.getCurrentHp() < HP ) {
					HP = receptionAllyData.getMaxHP();
				}
				receptionAllyData.setCurrentHp( HP );
				this.recoveryMessage = receptionAllyData.getName() + "は" + recovery + "のHPを回復した!";
			
			//全回復魔法
			}else if( magic.getPercentage() == 1 ){
				int HP = receptionAllyData.getMaxHP();
				receptionAllyData.setCurrentHp( HP );
				this.recoveryMessage = receptionAllyData.getName() + "は全快した!";
			}
		
		//毒を治す魔法
		}else if( buffCategory.equals( "poison" ) ) {
			
			//対象キャラクターの状態異常を取得
			Set<Status> statusSet = receptionAllyData.getStatusSet();
			
			//毒状態かチェック
			Long sts = statusSet.stream()
					.filter( s -> s.getName().equals( "毒" ))
					.count();
			int size = statusSet.size();
			
			//状態異常が毒のみ
			if( sts == 1 && size == 1 ) {
				statusSet.clear();
				statusSet.add( new Normal() );
				this.resultMessage = receptionAllyData.getName() + "の毒が治った!!";
				
			//状態異常が毒以外にもある。
			}else if( sts == 1 && size > 1 ) {
				statusSet = allyData.getStatusSet()
						.stream()
						.filter( s -> !s.getName().equals( "毒" ) )
						.collect( Collectors.toSet() );
				this.resultMessage = receptionAllyData.getName() + "の毒が治った!!";
				
			//毒状態ではない。
			}else{
				this.resultMessage = receptionAllyData.getName() + "に効果はなかった…";
			}
			
			receptionAllyData.setStatusSet( statusSet );
			
		//蘇生魔法
		}else{
			
			//確定蘇生魔法
			if( magic.getPercentage() == 1 ) {
				int HP = receptionAllyData.getMaxHP();
				receptionAllyData.setCurrentHp( HP );
				receptionAllyData.resuscitation();
				this.recoveryMessage = receptionAllyData.getName() + "は完全に生き返った!!";
			
			//確率蘇生魔法
			}else{

				//蘇生判定処理
				Random random = new Random();
				int judgement = random.nextInt( 3 );
				
				//蘇生成功
				if( judgement > 0 ) {
					int HP = receptionAllyData.getMaxHP() / 2;
					receptionAllyData.setCurrentHp( HP );
					
					//その他の蘇生処理はキャラクターデータのオブジェクト内で実行
					receptionAllyData.resuscitation();
					this.recoveryMessage = receptionAllyData.getName() + "は生き返った!!";
				
				//蘇生失敗
				}else{
					this.recoveryMessage = receptionAllyData.getName() + "は生き返らなかった･･･";
				}
			}
			
		}
		
		return receptionAllyData;
		
	}
	
}
