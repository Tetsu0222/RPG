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

import com.example.rpg2.action.Attack;
import com.example.rpg2.action.EnemyAction;
import com.example.rpg2.action.SortingAttackAction;
import com.example.rpg2.action.SortingRecoveryAction;
import com.example.rpg2.action.TaregetEnemyAction;
import com.example.rpg2.action.TargetAllyAction;
import com.example.rpg2.action.endskill.SortingEndSkill;
import com.example.rpg2.action.startskill.SortingStartSkill;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.entity.Skill;
import com.example.rpg2.process.BadStatusAfter;
import com.example.rpg2.process.BadStatusBefore;
import com.example.rpg2.process.CancelDefense;
import com.example.rpg2.process.ChoiceDefense;
import com.example.rpg2.process.ConsumptionMP;
import com.example.rpg2.process.IsEndSkillStop;
import com.example.rpg2.process.IsStartSkillStop;

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
	
	Random random = new Random();
	
	//コンストラクタ(戦闘不能と蘇生の関係で、複数のコレクションで敵味方の座標とオブジェクトを管理)
	public Battle( Set<AllyData> partySet , Set<MonsterData> monsterDataSet ) {
		
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
		
	}
	
	
	//------------------------------------------------------------------------------
	//---------------行動選択処理(選択中の行動と対象者の表示に必要)-----------------
	//------------------------------------------------------------------------------
	
	//通常攻撃が選択された場合の事前処理
	public void selectionAttack( Integer myKeys , Integer key ) {
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key );
		targetMap.put( myKeys , target );
	}
	
	//味方への魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Integer key , Magic magic ) {
		Target target = new Target ( partyMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
	}
	
	//味方への全体魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Magic magic ) {
		//最後の引数はオーバーロード用のダミー
		Target target = new Target ( partyMap , targetSetAlly , myKeys ,  magic , 1 );
		targetMap.put( myKeys , target );
	}
	
	//敵への魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Integer key , Magic magic ) {
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
	}
	
	//敵への全体魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Magic magic ) {
		Target target = new Target( monsterDataMap , targetSetEnemy , myKeys ,  magic );
		targetMap.put( myKeys , target );
	}
	
	//味方への特技が選択された場合の事前処理
	public void selectionAllySkill( Integer myKeys , Integer key , Skill skill ) {
		Target target = new Target ( partyMap.get( key ) , myKeys , key , skill );
		targetMap.put( myKeys , target );
	}
	
	//味方への全体特技が選択された場合の事前処理
	public void selectionAllySkill( Integer myKeys , Skill skill ) {
		//最後の引数はオーバーロード用のダミー
		Target target = new Target ( partyMap , targetSetAlly , myKeys ,  skill , 1 );
		targetMap.put( myKeys , target );
	}
	
	//敵への特技が選択された場合の事前処理
	public void selectionMonsterSkill( Integer myKeys , Integer key , Skill skill ) {
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key , skill );
		targetMap.put( myKeys , target );
	}
	
	//敵への全体特技が選択された場合の事前処理
	public void selectionMonsterSkill( Integer myKeys , Skill skill ) {
		Target target = new Target( monsterDataMap , targetSetEnemy , myKeys ,  skill );
		targetMap.put( myKeys , target );
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
	public void startBattle() {
		
		
		//ターンの最初に発動する効果を処理
		this.startSkill();
				
		//敵味方が入り乱れて素早さ順に行動
        for( Entry<Integer, Integer> entry : turnList ) {
        	
        	//ターン中に敵か味方のいずれかが全滅している場合は、戦闘を終了させる。
        	if( targetSetEnemy.size() == 0 || targetSetAlly.size() == 0 ) {
        		break;
        	}
        	
        	//行動するキャラの座標を抽出
            int key = entry.getKey();
            
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
    				continue;
    			}
    			
				//行動不能系の状態異常の所持数をチェック
    			BadStatusBefore badStatusBefore = new BadStatusBefore();
    			Integer juds = badStatusBefore.execution( allyData );
    			
    			if( badStatusBefore.getMessage() != null ) {
    				this.mesageList.add( badStatusBefore.getMessage() );
    			}
    			
    			//行動不能と判定された状態異常が1つ以上あれば処理中断
    			if( juds > 0 ) {
    				this.badStatusAfter( allyData, key );
    				continue;
    			}
    			
    			
    			//通常攻撃の処理
				if( movementPattern.equals( "attack" )) {
					
					//通常攻撃を生成
					TaregetEnemyAction at = new Attack( allyData );
					
					//通常攻撃を実施
					this.mesageList.add( at.getStratMessage() );
					this.singleAttack( at,  target , key , magic , skill );

				//回復・補助魔法の処理
				}else if( movementPattern.equals( "targetally" ) || movementPattern.equals( "resuscitationmagic" ) || movementPattern.equals( "resuscitationskill" ) ) {
					
					//回復or補助or蘇生の魔法か特技か判定して該当オブジェクトを生成
					TargetAllyAction targetAllyAction = SortingRecoveryAction.sortingCreateRecoveryAction( allyData , magic , skill );
					
					//行動を宣言
					this.mesageList.add( targetAllyAction.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( targetAllyAction.isNotEnoughMp() ){
						this.mesageList.add( targetAllyAction.getNotEnoughMpMessage() );
						isMpEmpty = true;
						
					//MP判定OK
					}else{
						
						//魔法特技の指定回数分の処理
						for( int i = 0 ; i < SortingRecoveryAction.actions ; i++ ){
							
							//無差別回復
							if( SortingRecoveryAction.targetRandom ) {
								List<Integer> targetList = new ArrayList<Integer>( targetSetAlly );
								target = random.nextInt( targetList.size() );
								this.singleSupport( targetAllyAction , target , key );
							
							//蘇生
							}else if( SortingRecoveryAction.isResuscitation ) {
								
								//全体蘇生魔法の処理
								if( targetMap.get( key ).getTargetSetAlly() != null ) {
									this.resuscitationMagicExecution( targetAllyAction , -1 , key );
									
								//単体蘇生魔法の処理
								}else{
									this.resuscitationMagicExecution( targetAllyAction , target , key );
								}
								
							//全体回復魔法の処理
							}else if( targetMap.get( key ).getTargetSetAlly() != null ) {
								this.generalSupport( targetAllyAction , key );
								
							//単体回復魔法の処理
							}else{
								this.singleSupport( targetAllyAction , target , key );
							}
						}
					}
					
				//攻撃・妨害の処理
				}else if( movementPattern.equals( "targetenemy" )) {
					
					//行動用のオブジェクトと攻撃回数を生成(特技か魔法を判定して合致するものを生成)
					TaregetEnemyAction taregetEnemyAction = SortingAttackAction.sortingCreateAttackAction( allyData , magic , skill );
					
					//行動を宣言
					this.mesageList.add( taregetEnemyAction.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( taregetEnemyAction.isNotEnoughMp() ){
						this.mesageList.add( taregetEnemyAction.getNotEnoughMpMessage() );
						isMpEmpty = true;
						
					//MP判定OK
					}else{
						
						//魔法特技の攻撃回数分の処理
						for( int i = 0 ; i < SortingAttackAction.actions ; i++ ){
							
							//対象撃破時のターゲット自動変更のために再生成
							taregetEnemyAction = SortingAttackAction.sortingRegenerationAttackAction( allyData , magic , skill );
							
							//無差別攻撃
							if( SortingAttackAction.targetRandom ) {
								List<Integer> targetList = new ArrayList<Integer>( targetSetEnemy );
								target = random.nextInt( targetList.size() ) + 4;
								this.singleAttack( taregetEnemyAction , target , key , magic , skill );
								
							//全体攻撃の処理
							}else if( targetMap.get( key ).getTargetSetEnemy() != null ) {
								this.generalAttack( taregetEnemyAction , key );

							//単体攻撃の処理
							}else{
								this.singleAttack( taregetEnemyAction , target , key , magic , skill );
							}
						}
					}
				}
				
				if( !isMpEmpty ) {
					//MP消費処理
					allyData = ConsumptionMP.consumptionMP( allyData , magic , skill );
					partyMap.put( key , allyData );
				}

				//行動終了後に作用する状態異常の処理
				this.badStatusAfter( allyData, key );
		        
				
	        //----------------------------------------------------
	        //------------------敵側の処理------------------------
	        //----------------------------------------------------
            }else{
            	
            	//行動対象のモンスターのデータを生成
    			MonsterData monsterData = monsterDataMap.get( key );
    			
    			//ターン中に死亡している場合は処理中断
    			if( monsterData.getSurvival() == 0 ) {
    				continue;
    			}
    			
				//行動不能系の状態異常の所持数をチェック
    			BadStatusBefore badStatusBefor = new BadStatusBefore();
    			Integer juds = badStatusBefor.execution( monsterData );
    			
    			if( badStatusBefor.getMessage() != null ) {
    				this.mesageList.add( badStatusBefor.getMessage() );
    			}
    			
    			//行動不能と判定された状態異常が1つ以上あれば処理中断
    			if( juds > 0 ) {
    				this.badStatusAfter( monsterData , key );
    				continue;
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
    			this.badStatusAfter( monsterData , key );
    		}
        }
        
        //ターン終了後に処理する効果
        this.endSkill();
	}
	
	//-----------------------------------------------------------------------------------------------------------------------
	
	
	
	//---------------------------------------------別クラスへ分ける予定------------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	//-----------------------------------------戦闘処理を補助するメソッド群--------------------------------------------------
	//-----------------------------------------------------------------------------------------------------------------------
	
	//単体攻撃のメソッド
	public void singleAttack( TaregetEnemyAction taregetEnemyAction , Integer target , Integer key , Magic magic , Skill skill ) {
		
		//攻撃対象のオブジェクトを取得
		MonsterData monsterData = monsterDataMap.get( target );
		
		//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
		if( monsterData.getSurvival() == 0 ) {
			//生存している敵エネミーセットから座標を取得
			target = targetSetEnemy.stream().findAny().orElse( 0 );
			monsterData = monsterDataMap.get( target );
		}
		
		//攻撃処理と結果の格納
		monsterData = taregetEnemyAction.action( monsterData );
		monsterDataMap.put( target , monsterData );
		
		//ダメージがあれば表示に追加
		if( taregetEnemyAction.getDamageMessage() != null ) {
			this.mesageList.add( taregetEnemyAction.getDamageMessage() );
		}
		
		//状態異常が伴う場合か対象を倒した場合は、結果を表示に追加
		if( taregetEnemyAction.getResultMessage() != null ) {
			this.mesageList.add( taregetEnemyAction.getResultMessage() );
		}
		
		//攻撃で対象を倒した場合の処理
		if( monsterData.getCurrentHp() == 0 ) {
			
			//敵リストから対象を削除
			targetSetEnemy.remove( target );
			
			//敵が全滅していなければ、別対象へ通常攻撃でターゲットを変更しておく。
        	if( targetSetEnemy.size() != 0 ) {
        		
        		//ターゲット座標を取得
        		target = targetSetEnemy.stream().findAny().orElseThrow();
        		
        		//ターゲットを再設定
        		if( magic != null ) {
        			this.selectionMonsterMagic( key , target , magic );
        		}else if( skill != null ) {
        			this.selectionMonsterSkill( key , target , skill );
        		}else{
        			this.selectionAttack( key , target );
        		}
        	}
		}
	}
	
	
	//単体回復・補助のメソッド
	public void singleSupport( TargetAllyAction targetAllyAction , Integer target , Integer key ) {
		
		//対象の味方キャラクターのオブジェクトを取得
		AllyData receptionAllyData = partyMap.get( target );
		
		//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
		if( receptionAllyData.getSurvival() == 0 ) {
			target = targetSetAlly.stream().findAny().orElse( 0 );
			this.selectionAllyMagic( key , target , targetMap.get( key ).getExecutionMagic() );
		}
		
		//回復・補助魔法の処理と結果の格納
		receptionAllyData = targetAllyAction.action( partyMap.get( target ) );
		partyMap.put( target , receptionAllyData );
		
		//回復効果があれば表示に追加
		if( targetAllyAction.getRecoveryMessage() != null ) {
			this.mesageList.add( targetAllyAction.getRecoveryMessage() );
		}
		
		//状態異常の治癒があれば結果に追加
		if( targetAllyAction.getResultMessage() != null ) {
			this.mesageList.add( targetAllyAction.getResultMessage() );
		}
	}
	
	
	//全体攻撃
	public void generalAttack( TaregetEnemyAction taregetEnemyAction , Integer key ) {
		
		//targetを敵全体へ変更
		for( Integer target : targetSetEnemy ) {
			
			//撃破メッセージを初期化
			taregetEnemyAction.setResultMessage();
			
			MonsterData monsterData = taregetEnemyAction.action( monsterDataMap.get( target ) );
			this.monsterDataMap.put( target , monsterData );
			
			//ダメージがあれば表示に追加
			if( taregetEnemyAction.getDamageMessage() != null ) {
				this.mesageList.add( taregetEnemyAction.getDamageMessage() );
			}
			
			//状態異常が伴う場合か対象を倒した場合は、結果を表示に追加
			if( taregetEnemyAction.getResultMessage() != null ) {
				this.mesageList.add( taregetEnemyAction.getResultMessage() );
			}
		}
		
		//全体攻撃後、敵対象の生存チェック
		List<Integer> deathList = targetSetEnemy.stream()	//remove()の特性上、別リストへ置換
						.filter( s -> monsterDataMap.get( s ).getSurvival() == 0 )
						.collect( Collectors.toList() );
		
		//残存勢力のみに置換
		deathList.stream().forEach( s -> targetSetEnemy.remove( s ) );
		
	}
	
	
	//全体回復・補助のメソッド
	public void generalSupport( TargetAllyAction targetAllyAction , Integer key) {
		
		//ターゲットを全体に変更
		for( Integer target : targetSetAlly ) {
			AllyData receptionAllyData = targetAllyAction.action( partyMap.get( target ) );
			partyMap.put( target , receptionAllyData );
			
			//回復効果があれば表示に追加
			if( targetAllyAction.getRecoveryMessage() != null ) {
				this.mesageList.add( targetAllyAction.getRecoveryMessage() );
			}
			
			//状態異常の治癒があれば結果に追加
			if( targetAllyAction.getResultMessage() != null ) {
				this.mesageList.add( targetAllyAction.getResultMessage() );
			}
		}
	}
	
	
	//蘇生魔法の処理メソッド
	public void resuscitationMagicExecution( TargetAllyAction targetAllyAction , Integer target , Integer key ) {
		
		//全体魔法の処理
		if( target < 0 ) {
			
			//生死問わず、全体に作用するようにリストを生成
			List<Integer> list = new ArrayList<>(partyMap.keySet());
			
			//ターゲットを全体で再設定
			for( Integer target2 : list ) {
				
				//蘇生処理を実行
				AllyData receptionAllyData = targetAllyAction.action( partyMap.get( target2 ) );
				
				//蘇生判定の確認
				if( receptionAllyData.getSurvival() > 0 ) {
					
					//蘇生に成功していれば結果を格納
					partyMap.put( target2 , receptionAllyData );
					targetSetAlly.add( target2 );
				}
				this.mesageList.add( targetAllyAction.getRecoveryMessage() );
			}
			
		//単体魔法の処理
		}else{
			
			//蘇生処理を実行
			AllyData receptionAllyData = targetAllyAction.action( partyMap.get( target ) );
			
			//蘇生判定の確認
			if( receptionAllyData.getSurvival() > 0 ) {
				
				//蘇生に成功していれば結果を格納
				partyMap.put( target , receptionAllyData );
				targetSetAlly.add( target );
			}
			this.mesageList.add( targetAllyAction.getRecoveryMessage() );
		}
	}
	
	
	//味方側のダメージ系の状態異常処理（行動終了後に処理する状態異常のメソッド）
	public void badStatusAfter( AllyData allyData , Integer key ) {
		BadStatusAfter badStatusAfter = new BadStatusAfter( targetSetAlly , targetMap , targetSetEnemy );
		this.partyMap = badStatusAfter.execution( partyMap , allyData , key );
		this.targetSetAlly = badStatusAfter.getTargetSetAlly();
		this.targetMap     = badStatusAfter.getTargetMap();
		
		//自然治癒メッセージを追加
		if( badStatusAfter.getRecoveryMessage() != null ) {
			this.mesageList.add( badStatusAfter.getRecoveryMessage() );
		}
		
		//状態異常のメッセージを追加
		if( badStatusAfter.getResultMessage() != null ) {
			this.mesageList.add( badStatusAfter.getResultMessage() );
		}
		
		//状態異常のダメージで死亡した場合のメッセージを追加
		if( badStatusAfter.getDedMessage() != null ) {
			this.mesageList.add( badStatusAfter.getDedMessage() );
		}
	}
	
	
	//敵側のダメージ系の状態異常処理（行動終了後に処理する状態異常のメソッド）
	public void badStatusAfter( MonsterData monsterData , Integer key ) {
		BadStatusAfter badStatusAfter = new BadStatusAfter( targetSetAlly , targetMap , targetSetEnemy );
		this.monsterDataMap = badStatusAfter.execution( monsterDataMap , monsterData , key );
		this.targetSetEnemy = badStatusAfter.getTargetSetEnemy();
		
		//自然治癒メッセージを追加
		if( badStatusAfter.getRecoveryMessage() != null ) {
			this.mesageList.add( badStatusAfter.getRecoveryMessage() );
		}
		
		//状態異常のメッセージを追加
		if( badStatusAfter.getResultMessage() != null ) {
			this.mesageList.add( badStatusAfter.getResultMessage() );
		}
		
		//状態異常のダメージで死亡した場合のメッセージを追加
		if( badStatusAfter.getDedMessage() != null ) {
			this.mesageList.add( badStatusAfter.getDedMessage() );
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
			
			Target target = targetMap.get(index);
			AllyData allyData = partyMap.get( index );
			
			//防御の発動処理
			if( target.getSkillName().equals( "防御" )) {
				allyData = ChoiceDefense.choiceDefense( allyData );
				partyMap.put( index , allyData );
			}
			
			//スタートスキルを所持しつつ行動不能系の状態異常がなければ続行
			if( IsStartSkillStop.isStartSkillStop( allyData ) && !allyData.getTurnStartSkillSet().contains( "なし" )) {
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
			if( IsEndSkillStop.isEndSkillStop( allyData ) && !allyData.getTurnEndSkillSet().contains( "なし" )) {
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
