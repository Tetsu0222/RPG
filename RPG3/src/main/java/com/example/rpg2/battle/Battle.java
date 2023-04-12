package com.example.rpg2.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.rpg2.action.EnemyAction;
import com.example.rpg2.action.endskill.SortingEndSkill;
import com.example.rpg2.action.startskill.SortingStartSkill;
import com.example.rpg2.battle.support.BattleSupportAttack;
import com.example.rpg2.battle.support.BattleSupportRecovery;
import com.example.rpg2.battle.support.BattleSupportStatus;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.entity.Skill;
import com.example.rpg2.process.BadStatusBefore;
import com.example.rpg2.process.CancelDefense;
import com.example.rpg2.process.ChoiceDefense;
import com.example.rpg2.process.ConsumptionMP;
import com.example.rpg2.process.IsEndSkillStop;
import com.example.rpg2.process.IsStartSkillStop;
import com.example.rpg2.status.Confusion;

import lombok.Data;

@Data
public class Battle {
	
	
	//プレイアブルメンバーを管理
	private Map<Integer,AllyData> partyMap;
	
	//エネミーメンバーを管理
	private Map<Integer,MonsterData> monsterDataMap;
	
	//プレイアブルメンバーの行動選択を管理
	private Map<Integer,Target> targetMap;
	
	//味方の数とキーを管理、キャラの特定に使用
	private Set<Integer> targetSetAlly;
	
	//敵の数とキーを管理、同上
	private Set<Integer> targetSetEnemy;
	
	//表示するログを管理
	private List<String> mesageList = new ArrayList<>();
	
	//キーは敵味方混合、値は乱数補正後の素早さ。素早さ順で降順ソートしたリスト
	private List<Entry<Integer, Integer>> turnList;
	
	//グループ攻撃用のセット
	private List<String> enemyNameList;
	private List<String> allyNameList;
	
	Random random = new Random();
	
	private BattleSupportAttack battleSupportAttack;
	private BattleSupportRecovery battleSupportRecovery;
	private BattleSupportStatus battleSupportStatus;
	
	//コンストラクタ(戦闘不能と蘇生の関係で、複数のコレクションで敵味方の座標とオブジェクトを管理)
	public Battle( Set<AllyData> partySet , Set<MonsterData> monsterDataSet , List<String> allyNameList , List<String> enemyNameList ) {
		
		List<AllyData> partyList = new ArrayList<>( partySet );
		List<MonsterData> monsterDataList = new ArrayList<>( monsterDataSet );
		
		//プレイアブルメンバーを生成
		this.partyMap = IntStream.range( 0 , partyList.size() )
							.boxed()
							.collect( Collectors.toMap( s -> s , s -> partyList.get( s ) ));
		
		//エネミーデータを生成
		this.monsterDataMap = IntStream.range( 4 , monsterDataList.size() + 4 )
				.boxed()
				.collect( Collectors.toMap( s -> s , s -> monsterDataList.get( s - 4 ) ));
		
		//プレイアブルメンバーの初期行動を最初のエネミーへの通常攻撃で設定（例外対策）
		this.targetMap = IntStream.range( 0 , partyList.size() )
								.boxed()
								.collect( Collectors.toMap( s -> s ,
										s -> new Target( monsterDataMap.get( 4 )  , s  , 4 )));
		
		//味方と敵の座標リストをそれぞれ生成(各マップのキー数字とリンク）
		this.targetSetEnemy = new TreeSet<>( monsterDataMap.keySet() );
		this.targetSetAlly  = new TreeSet<>( partyMap.keySet() );
		
		//グループ攻撃用のセットを設定
		this.allyNameList  = allyNameList;
		this.enemyNameList = enemyNameList;
		
	}
	
	//サポートクラスの生成
	public void createSupport () {
		this.battleSupportAttack   = new BattleSupportAttack( this );
		this.battleSupportRecovery = new BattleSupportRecovery( this );
		this.battleSupportStatus   = new BattleSupportStatus( this );
	}
	
	
	//------------------------------------------------------------------------------
	//---------------行動選択処理(選択中の行動と対象者の表示に必要)-----------------
	//------------------------------------------------------------------------------
	
	//通常攻撃が選択された場合の事前処理
	public void selectionAttack( Integer myKeys , Integer key ) {
		battleSupportAttack.selectionAttack( myKeys , key );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//味方への魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Integer key , Magic magic ) {
		battleSupportRecovery.selectionAllyMagic( myKeys , key , magic );
		this.targetMap = battleSupportRecovery.getTargetMap();
	}
	
	//味方への全体魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Magic magic ) {
		battleSupportRecovery.selectionAllyMagic( myKeys , magic );
		this.targetMap = battleSupportRecovery.getTargetMap();
	}
	
	//敵への魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Integer key , Magic magic ) {
		battleSupportAttack.selectionMonsterMagic( myKeys , key , magic );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//敵へのグループ攻撃魔法が選択された時の処理
	public void selectionMonsterMagic( String name , Integer myKeys , Magic magic ) {
		battleSupportAttack.selectionMonsterMagic( name , myKeys , magic );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//敵への全体魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Magic magic ) {
		battleSupportAttack.selectionMonsterMagic( myKeys , magic );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//味方への特技が選択された場合の事前処理
	public void selectionAllySkill( Integer myKeys , Integer key , Skill skill ) {
		battleSupportRecovery.selectionAllySkill( myKeys , key , skill );
		this.targetMap = battleSupportRecovery.getTargetMap();
	}
	
	//味方への全体特技が選択された場合の事前処理
	public void selectionAllySkill( Integer myKeys , Skill skill ) {
		battleSupportRecovery.selectionAllySkill( myKeys , skill );
		this.targetMap = battleSupportRecovery.getTargetMap();
	}
	
	//敵への特技が選択された場合の事前処理
	public void selectionMonsterSkill( Integer myKeys , Integer key , Skill skill ) {
		battleSupportAttack.selectionMonsterSkill( myKeys , key , skill );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//敵へのグループ攻撃特技が選択された時の処理
	public void selectionMonsterSkill( String name , Integer myKeys , Skill skill ) {
		battleSupportAttack.selectionMonsterSkill( name , myKeys , skill );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//敵への全体特技が選択された場合の事前処理
	public void selectionMonsterSkill( Integer myKeys , Skill skill ) {
		battleSupportAttack.selectionMonsterSkill( myKeys ,  skill );
		this.targetMap = battleSupportAttack.getTargetMap();
	}
	
	//防御を選択
	public void selectionDefense( Integer myKeys ) {
		Target target = new Target( myKeys , "防御" );
		targetMap.put( myKeys , target );
	}
	
	//--------------------------------------------------------------------------------------
	
	
	
	//行動順を決定
	public void turn() {
		
		//各キャラの座標と素早さで構成されたマップ
		Map<Integer,Integer> turnMap = new HashMap<>();
		
		//素早さの補正用
		Random random = new Random();
		
		//味方の座標と素早さをマップへ格納
		for( Integer index : targetSetAlly ) {
			Integer spe   = partyMap.get( index ).getCurrentSPE();
			spe += random.nextInt( spe / 2 );
			turnMap.put( index , spe );
		}
		
		//敵の座標と素早さをマップへ格納
		for( Integer index : targetSetEnemy ) {
			Integer spe   = monsterDataMap.get( index ).getCurrentSPE();
			spe += random.nextInt( spe / 2 );
			turnMap.put( index , spe );
		}
		
		//敵味方の混合マップからエントリーを抽出、各キャラの座標が素早さの高い順（降順）でソートされているリストを生成
		this.turnList = new ArrayList<Entry<Integer, Integer>>( turnMap.entrySet() );
        Collections.sort( turnList , new Comparator<Entry<Integer, Integer>>() {
            public int compare( Entry<Integer, Integer> obj1 , Entry<Integer, Integer> obj2 )
            {
            	return obj2.getValue().compareTo( obj1.getValue() );
            }
        });
	}
	
	
	
	//------------------------------------------------------------------------------
	//----------------------------------戦闘開始------------------------------------
	//------------------------------------------------------------------------------
	public void startBattle( Integer key ) {
		
		//----------------------------------------------------
        //------------------味方側の処理----------------------
        //----------------------------------------------------
		if( partyMap.get( key ) != null ) {
    		AllyData allyData = partyMap.get( key );
    		Integer  target	  = targetMap.get( key ).getSelectionId();
    		String   movementPattern = targetMap.get( key ).getCategory();
			Skill skill = targetMap.get( key ).getExecutionSkill();
			Magic magic = targetMap.get( key ).getExecutionMagic();
			boolean isMpEmpty = false;

    		//ターン中に死亡している場合は、処理を中断(カウンターなどを想定)
    		if( allyData.getSurvival() == 0 ) {
    			movementPattern = "";
    		}
    			
			//行動不能系の状態異常の所持数をチェック
    		BadStatusBefore badStatusBefore = new BadStatusBefore();
    		Integer juds = badStatusBefore.execution( allyData );
    			
    		//行動不能の状態異常があれば、そのメッセージを格納
    		if( badStatusBefore.getMessage() != null ) {
    			this.mesageList.add( badStatusBefore.getMessage() );
    		}
    			
    		//行動不能と判定された状態異常が1つ以上あれば処理中断
    		if( juds > 0 ) {
    			battleSupportStatus.badStatusAfter( allyData, key );
    			movementPattern = "";
    		}
    		
    		//混乱中の場合の処理
    		Confusion confusion = new Confusion();
    		if( allyData.getStatusSet().contains( confusion )) {
    			movementPattern = "confusion";
    		}
    			
    		//通常攻撃の処理
			if( movementPattern.equals( "attack" )) {
				
				//通常攻撃の処理実行
				battleSupportAttack.normalAttack( target , key , magic , skill , allyData );
				
				//処理結果を獲得
				this.result();

				
			//回復・補助魔法の処理
			}else if( movementPattern.equals( "targetally" ) || movementPattern.equals( "resuscitationmagic" ) || movementPattern.equals( "resuscitationskill" ) ) {
				
				//回復・補助・蘇生の魔法か特技の処理、MPが足りていたかどうかの結果が返る。
				isMpEmpty = battleSupportRecovery.magicOrSkillRecovery( allyData , magic , skill , target , key );
				
				//処理結果を格納
				this.resultRecovery();
					
				
			//攻撃・妨害の処理
			}else if( movementPattern.equals( "targetenemy" )) {
				
				//攻撃魔法か特技を発動、MPが足りていたかどうかの結果が返る。
				isMpEmpty = battleSupportAttack.magicOrSkillAttack( allyData , magic , skill , target , key );
				
				//処理結果を取得
				this.result();
				
				
			//防御選択時の行動
			}else if( movementPattern.equals( "defense" )) {
				this.mesageList.add( allyData.getName() + "は防御している" );
				
				
			//混乱中の行動
			}else if( movementPattern.equals( "confusion" )) {
				battleSupportStatus.confusion( allyData );
			}
				
			
			//MP消費処理
			if( !isMpEmpty ) {
				//MP消費処理
				allyData = ConsumptionMP.consumptionMP( allyData , magic , skill );
				partyMap.put( key , allyData );
			}

			//行動終了後に作用する状態異常の処理
			battleSupportStatus.badStatusAfter( allyData, key );
			this.resultStatus();

				
		//----------------------------------------------------
		//------------------敵側の処理------------------------
		//----------------------------------------------------
		}else if( monsterDataMap.get( key ) != null ){
            	
            //行動対象のモンスターのデータを生成
    		MonsterData monsterData = monsterDataMap.get( key );
    			
			//行動不能系の状態異常の所持数をチェック
    		BadStatusBefore badStatusBefor = new BadStatusBefore();
    		Integer juds = badStatusBefor.execution( monsterData );
    			
    		if( badStatusBefor.getMessage() != null ) {
    			this.mesageList.add( badStatusBefor.getMessage() );
    		}
    			
			//味方のセットをリストへ変換
			List<Integer> targetList = new ArrayList<Integer>( targetSetAlly );
    			
    		//モンスターの行動回数を設定
    		List<Integer> actionsList = monsterData.getActionsList();
    		int actions = actionsList.get( 0 );
    			
    		//行動回数がランダムの場合の処理
    		if( actionsList.size() > 1 ) {
    			Random random = new Random();
    			int index = random.nextInt( actionsList.size() );
    			actions = actionsList.get( index );
    		}
    			
    		//行動不能と判定された状態異常が1つ以上あれば処理中断
    		if( juds > 0 ) {
    			battleSupportStatus.badStatusAfter( monsterData , key );
    			actions = 0;
    		}
    			
    		//ターン中に死亡している場合は、処理を中断(カウンターなどを想定)
    		if( monsterData.getSurvival() == 0 ) {
    			actions = 0;
    		}
    			
    		//複数行動に対応
    		for( int a = 0 ; a < actions ; a++ ) {
    				
    			EnemyAction enemyAction = new EnemyAction();
    				
    			//モンスターの行動を決定
	    		enemyAction.decision( monsterData );
	    			
	    		//ターン中に死亡してた場合は、行動処理を上書き(カウンターや反射ダメージなどを想定）
	    		if( monsterData.getSurvival() == 0 ) {
	    			enemyAction.setRange( "death" );
	    			enemyAction.setPattern( "death" );
	    			break;
	    		}
	    			
	            //ターン中に敵か味方のいずれかが全滅している場合は、行動を終了させる。
	            if( targetSetEnemy.size() == 0 || targetSetAlly.size() == 0 ) {
	            	break;
	            }
	    			
	    		//単体攻撃処理
	    		if( enemyAction.getRange().equals( "single" )){
	    			this.singleAttack( targetList , enemyAction );
	    				
	    		//全体攻撃を処理
	    		}else if( enemyAction.getRange().equals( "whole" )){
	    			this.wholeAttack( targetList , enemyAction );
	
		    	//ミス系
		    	}else if( enemyAction.getPattern().equals( "miss" )){
		    		enemyAction.noAction();
		    		mesageList.add( monsterData.getName() + "の攻撃!!" );
		    		mesageList.add( "しかし、攻撃は外れてしまった…" );
		    			
		    	//死亡時
		    	}else{
		    		enemyAction.noAction();
		    	}
    		}
    			
    		//行動終了後の状態異常を処理
    		battleSupportStatus.badStatusAfter( monsterData , key );
    		this.resultStatus();
    	}
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	
	
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------戦闘処理を補助するメソッド群--------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	
	//攻撃の結果取得メソッド
	public void result() {
		this.targetSetAlly  = battleSupportAttack.getTargetSetAlly();
		this.targetSetEnemy = battleSupportAttack.getTargetSetEnemy();
		this.monsterDataMap = battleSupportAttack.getMonsterDataMap();
		this.mesageList     = battleSupportAttack.getMesageList();
		this.targetMap      = battleSupportAttack.getTargetMap();
		this.enemyNameList  = battleSupportAttack.getEnemyNameList();
		this.partyMap		= battleSupportAttack.getPartyMap();
	}
	
	
	//回復補助蘇生の結果取得メソッド
	public void resultRecovery() {
		this.targetSetAlly  = battleSupportRecovery.getTargetSetAlly();
		this.targetSetEnemy = battleSupportRecovery.getTargetSetEnemy();
		this.monsterDataMap = battleSupportRecovery.getMonsterDataMap();
		this.mesageList     = battleSupportRecovery.getMesageList();
		this.targetMap      = battleSupportRecovery.getTargetMap();
		this.enemyNameList  = battleSupportRecovery.getEnemyNameList();
		this.partyMap		= battleSupportRecovery.getPartyMap();
	}
	
	
	//状態異常の結果取得メソッド
	public void resultStatus() {
		this.targetSetAlly  = battleSupportStatus.getTargetSetAlly();
		this.targetSetEnemy = battleSupportStatus.getTargetSetEnemy();
		this.monsterDataMap = battleSupportStatus.getMonsterDataMap();
		this.targetMap      = battleSupportStatus.getTargetMap();
		this.enemyNameList  = battleSupportStatus.getEnemyNameList();
		this.partyMap		= battleSupportStatus.getPartyMap();
		
		for( String message : battleSupportStatus.getMesageList() ) {
			this.mesageList.add( message );
		}
	}
	
	
	//敵の単体攻撃を処理するメソッド
	public void singleAttack( List<Integer> targetList , EnemyAction enemyAction ) {
		
		//単体攻撃を処理
		AllyData allyData = enemyAction.attackSkillSingle( partyMap , targetList );
		
		//行動開始のメッセージを追加
		mesageList.add( enemyAction.getStartMessage() );
		
		//ダメージがあれば表示に追加
		if( enemyAction.getBattleMessage() != null ) {
			mesageList.add( enemyAction.getBattleMessage() );
		}
		
		//状態異常があれば表示に追加
		if( enemyAction.getBuffMessage() != null ) {
			mesageList.add( enemyAction.getBuffMessage() );
		}
		
		//攻撃で味方が倒れた場合の処理の処理結果を反映
		if( allyData.getSurvival() == 0 ) {
			targetSetAlly.remove( enemyAction.getTargetId() );
			targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
			partyMap.put( enemyAction.getTargetId() , allyData );
			mesageList.add( enemyAction.getDedMessage() );
		
		//行動の処理結果を反映
		}else{
			partyMap.put( enemyAction.getTargetId() , allyData );
		}
	}
	
	
	//敵の全体攻撃を処理するメソッド
	public void wholeAttack( List<Integer> targetList , EnemyAction enemyAction ) {
		
		//行動開始のメッセージを表示に追加
		mesageList.add( enemyAction.getStartMessage() );
		
		//味方全体へ処理を繰り返す。
		for( int j = 0 ; j < targetList.size() ; j++ ) {
			
			int targetId = targetList.get( j );
			AllyData allyData = enemyAction.attackSkillWhole( partyMap , targetId );
			
			//ダメージがあれば表示に追加
			if( enemyAction.getBattleMessage() != null ) {
				mesageList.add( enemyAction.getBattleMessage() );
			}
			
			//状態異常があれば表示に追加
			if( enemyAction.getBuffMessage() != null ) {
				mesageList.add( enemyAction.getBuffMessage() );
			}
			
			//攻撃結果で味方が倒れた場合の処理とその結果の格納
			if( allyData.getSurvival() == 0 ) {
				targetSetAlly.remove( enemyAction.getTargetId() );
				targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
				partyMap.put( enemyAction.getTargetId() , allyData );
				if( enemyAction.getDedMessage() != null ) {
					mesageList.add( enemyAction.getDedMessage() );
				}
			
			//処理結果を格納
			}else{
				partyMap.put( enemyAction.getTargetId() , allyData );
			}
		}
	}
	
	
	//ターンスタート時の処理
	public void startSkill() {
		
		for( int index : targetSetAlly ) {
			
			Target   target   = targetMap.get( index );
			AllyData allyData = partyMap.get( index );
			
			//防御の発動処理
			if( target.getSkillName().equals( "防御" )) {
				allyData = ChoiceDefense.choiceDefense( allyData );
				partyMap.put( index , allyData );
			}
			
			//スタートスキルを所持しつつ行動不能系の状態異常がなければ続行
			if( IsStartSkillStop.isStartSkillStop( allyData ) && allyData.getTurnStartSkillSet() != null) {
				AllyData allyData2 = partyMap.get( index ); //実質的にファイナルとするため再初期化
				allyData.getTurnStartSkillSet().stream()
				.map( s -> SortingStartSkill.sortingSkill( s ))
				.map( s -> s.action( allyData2 ) )
				.peek( s -> partyMap.put( index , allyData2 ))
				.filter( s -> s.getStartSkillMessage() != null )
				.peek( s -> this.mesageList.add( s.getStartSkillMessage() ))
				.forEach( s -> s.setStartSkillMessage( null ));
			}
		}
	}
	
	
	//ターンエンド時の処理
	public void endSkill() {
		
		for( int index : targetSetAlly ) {
			
			//防御状態の解除（行動不能でも実行）
			AllyData allyData = partyMap.get( index );
			allyData = CancelDefense.cancelDefense( allyData );
			partyMap.put( index , allyData );
			
			//エンドスキルを所持しつつ行動不能系の状態異常がなければ続行
			if( IsEndSkillStop.isEndSkillStop( allyData ) && allyData.getTurnEndSkillSet() != null ) {
				AllyData allyData2 = partyMap.get( index ); //実質的にファイナルとするため再初期化
				allyData2.getTurnEndSkillSet().stream()
				.map( s -> SortingEndSkill.sortingSkill( s ))
				.map( s -> s.action( allyData2 ) )
				.peek( s -> partyMap.put( index , allyData2 ))
				.filter( s -> s.getEndSkillMessage() != null )
				.peek( s -> this.mesageList.add( s.getEndSkillMessage() ))
				.forEach( s -> s.setEndSkillMessage( null ));
			}
		}
	}
	
}
