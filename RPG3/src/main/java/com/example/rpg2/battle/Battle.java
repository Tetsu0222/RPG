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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.rpg2.action.Attack;
import com.example.rpg2.action.RecoveryMagic;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.Dead;
import com.example.rpg2.status.Defense;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;

import lombok.Data;

@Data
public class Battle {
	
	
	//プレイアブルメンバーを管理
	Map<Integer,AllyData> partyMap;
	
	//エネミーメンバーを管理
	Map<Integer,MonsterData> monsterDataMap;
	
	//プレイアブルメンバーの行動選択を管理
	Map<Integer,Target> targetMap;
	
	//味方の数とキーを管理、キャラの特定に使用
	List<Integer> targetListAlly;
	
	//敵の数とキーを管理、同上
	List<Integer> targetListEnemy;
	
	//表示するログを管理
	List<String> mesageList = new ArrayList<>();
	
	//キーは敵味方混合、値は乱数補正後の素早さ。素早さ順で降順ソートしたリスト
	List<Entry<Integer, Integer>> turnList;
	
	
	//コンストラクタ
	public Battle( List<AllyData> partyList , List<MonsterData> monsterDataList ) {
		
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
		this.targetListEnemy = new ArrayList<>( monsterDataMap.keySet() );
		this.targetListAlly  = new ArrayList<>( partyMap.keySet() );
	}
	
	
	//------------------------------------------------------------------------------
	//---------------行動選択処理(選択中の行動と対象者の表示に必要)-----------------
	//------------------------------------------------------------------------------
	
	//通常攻撃が選択された場合の事前処理
	public void selectionAttack( Integer myKeys ,  Integer key ) {
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key );
		targetMap.put( myKeys , target );
		this.cancelDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//味方への魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Integer key , Magic magic ) {
		Target target = new Target ( partyMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
		this.cancelDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//味方への全体魔法が選択された場合の事前処理
	public void selectionAllyMagic( Integer myKeys , Magic magic ) {
		//最後の引数はオーバーロード用のダミー
		Target target = new Target ( partyMap , targetListAlly , myKeys ,  magic , 1 );
		targetMap.put( myKeys , target );
		this.cancelDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//敵への魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Integer key , Magic magic ) {
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
		this.cancelDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//敵への全体魔法が選択された場合の事前処理
	public void selectionMonsterMagic( Integer myKeys , Magic magic ) {
		Target target = new Target( monsterDataMap , targetListEnemy , myKeys ,  magic );
		targetMap.put( myKeys , target );
		this.cancelDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//防御を選択
	public void selectionDefense( Integer myKeys ) {
		Target target = new Target( myKeys , "防御" );
		targetMap.put( myKeys , target );
		this.choiceDefense( partyMap.get( myKeys ) , myKeys );
	}
	
	//--------------------------------------------------------------------------------------
	
	
	
	//行動順を決定
	public void turn() {
		
		//各キャラの座標と素早さで構成されたマップ
		Map<Integer,Integer> turnMap = new HashMap<>();
		
		//素早さの補正用
		Random random = new Random();
		
		//味方の座標と素早さをマップへ格納
		for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
			Integer index = targetListAlly.get( i );
			Integer spe   = partyMap.get( index ).getCurrentSPE();
			spe += random.nextInt( spe / 2 );
			turnMap.put( index , spe );
		}
		
		//敵の座標と素早さをマップへ格納
		for( int i = 0 ; i < targetListEnemy.size() ; i++ ) {
			Integer index = targetListEnemy.get( i );
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
		//敵味方が入り乱れて素早さ順に行動
        for( Entry<Integer, Integer> entry : turnList ) {
        	//ターン中に敵か味方のいずれかが全滅している場合は、戦闘を終了させる。
        	if( targetListEnemy.size() == 0 || targetListAlly.size() == 0 ) {
        		break;
        	}
        	//行動するキャラの座標を抽出
            int key = entry.getKey();
            
            //----------------------------------------------------
            //------------------味方側の処理----------------------
            //----------------------------------------------------
            if( partyMap.get( key ) != null ) {
    			Action   action   = new Action();
    			AllyData allyData = partyMap.get( key );
    			Integer  target	  = targetMap.get( key ).getSelectionId();
    			String   movementPattern = targetMap.get( key ).getCategory();

    			//ターン中に死亡している場合は、処理を中断
    			if( allyData.getSurvival() == 0 ) {
    				continue;
    			}
    			
				//行動不能系の状態異常の所持数をチェック
    			Integer juds = this.badStatusBefore( allyData , key );
    			
    			//行動不能系の状態異常が1つ以上あれば処理中断
    			if( juds > 0 ) {
    				this.badStatusAfter( allyData, key );
    				continue;
    			}
    			
    			
    			//通常攻撃の処理
				if( movementPattern.equals( "attack" )) {
					MonsterData monsterData = monsterDataMap.get( target );
					
					//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
					if( monsterData.getSurvival() == 0 ) {
						target = targetListEnemy.get( 0 );
						this.selectionAttack( key , target );
					}
					
					//通常攻撃を生成
					Attack at = new Attack();
					
					//通常攻撃を実施
					monsterData = at.action( allyData , monsterData );
					
					//通常攻撃のメッセージをセット
					mesageList.add( at.getStratMessage() );
					mesageList.add( at.getDamageMessage() );
					
					//通常攻撃の結果をデータ管理・反映マップへ格納
					monsterDataMap.put( target , monsterData );
					
					//攻撃で対象を倒した場合の処理
					if( monsterData.getCurrentHp() == 0 ) {
						targetListEnemy.remove( target );
			        	if( targetListEnemy.size() != 0 ) {
							target = targetListEnemy.get( 0 );
							this.selectionAttack( key , target );
			        	}
			        	mesageList.add( at.getResultMessage() );
					}
					
					
				//回復魔法の処理
				}else if( movementPattern.equals( "recoverymagic" )) {
					
					//回復魔法を生成
					RecoveryMagic recoveryMagic = new RecoveryMagic( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( recoveryMagic.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( recoveryMagic.isNotEnoughMp() ){
						this.mesageList.add( recoveryMagic.getNotEnoughMpMessage() );
					
					//MP判定OK
					}else{
						//全体回復魔法の処理
						if( targetMap.get( key ).getTargetListAlly() != null ) {
							for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
								target = targetListAlly.get( i );
								AllyData receptionAllyData = recoveryMagic.action( partyMap.get( target ) );
								partyMap.put( target , receptionAllyData );
								this.mesageList.add( recoveryMagic.getRecoveryMessage() );
							}
							
						//単体回復魔法の処理
						}else{
							//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
							if( allyData.getSurvival() == 0 ) {
								target = targetListAlly.get( 0 );
								this.selectionAllyMagic( key , target , targetMap.get( key ).getExecutionMagic() );
							}
							AllyData receptionAllyData = recoveryMagic.action( partyMap.get( target ) );
							partyMap.put( target , receptionAllyData );
							this.mesageList.add( recoveryMagic.getRecoveryMessage() );
						}
						
						//MP消費処理
						allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
						partyMap.put( key , allyData );
					}
				
					
				//攻撃魔法の処理(改修予定）
				}else if( movementPattern.equals( "attackmagic" )) {
					mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
					//MP判定
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						//全体攻撃の処理
						if( targetMap.get( key ).getTargetListEnemy() != null ) {
							for( int i = 0 ; i < targetListEnemy.size() ; i++ ) {
								target = targetListEnemy.get( i );
								attackMagicExecution( key , target , allyData , action );
							}
						//単体攻撃の処理(対象死亡時のターゲット変更は、呼び出し先のメソッドで実施)
						}else{
							attackMagicExecution( key , target , allyData , action );
						}
						
						//敵対象の生存チェックと死亡処理
						List<Integer> deathList = targetListEnemy.stream()	//remove()の特性上、別リストへ置換
										.filter( s -> monsterDataMap.get( s ).getSurvival() == 0 )
										.collect( Collectors.toList() );
						deathList.stream().forEach( s -> targetListEnemy.remove( s ) );
						
						//MP消費処理（別メソッド化予定）
						allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
						partyMap.put( key , allyData );
					}
				
					
				//補助魔法の処理(改修予定)
				}else if( movementPattern.equals( "buffmagic" )) {
					mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
					//MP判定処理
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						//全体補助魔法の処理
						if( targetMap.get( key ).getTargetListAlly() != null ) {
							for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
								target = targetListAlly.get( i );
								this.buffmagicExecution( key , target , allyData , action );
							}
						//単体補助魔法の処理
						}else{
							//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
							if( allyData.getSurvival() == 0 ) {
								target = targetListAlly.get( 0 );
								this.selectionAllyMagic( key , target , targetMap.get( key ).getExecutionMagic() );
							}
							this.buffmagicExecution( key , target , allyData , action );
						}
					//MP消費処理（別メソッド化予定）
					allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
					partyMap.put( key , allyData );
					}
					
					
				//蘇生魔法の処理(改修予定）
				}else if( movementPattern.equals( "resuscitationmagic" )) {
					mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
					//MP判定処理
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						//全体蘇生魔法の処理
						if( targetMap.get( key ).getTargetListAlly() != null ) {
							for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
								target = targetListAlly.get( i );
								this.resuscitationmagicExecution( key , target , allyData , action );
							}
						//単体蘇生魔法の処理
						}else{
							this.resuscitationmagicExecution( key , target , allyData , action );
						}
					//MP消費処理（別メソッド化予定）
					allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
					partyMap.put( key , allyData );
					}
					
					
				//妨害の処理(改修予定）
				}else if( movementPattern.equals( "debuffmagic" )) {
					mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
					//MP判定
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						//全体妨害の処理
						if( targetMap.get( key ).getTargetListEnemy() != null ) {
							for( int i = 0 ; i < targetListEnemy.size() ; i++ ) {
								target = targetListEnemy.get( i );
								this.debuffMagicMagicExecution( key , target , allyData , action );
							}
						//単体妨害の処理(対象死亡時のターゲット変更は、呼び出し先のメソッドで実施)
						}else{
							this.debuffMagicMagicExecution( key , target , allyData , action );
						}
						
						//MP消費処理（別メソッド化予定）
						allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
						partyMap.put( key , allyData );
					}
				}
				
				this.badStatusAfter( allyData, key );
				
				
	        //----------------------------------------------------
	        //------------------敵側の処理------------------------
	        //----------------------------------------------------
            }else{
    			MonsterData monsterData = monsterDataMap.get( key );
    			
    			//行動対象のモンスターの行動回数を設定
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
	    			enemyAction.decision( monsterData );
	    			
	    			//ターン中に死亡してた場合は、行動処理を上書き。
	    			if( monsterData.getSurvival() == 0 ) {
	    				enemyAction.setRange( "death" );
	    				enemyAction.setPattern( "death" );
	    				break;
	    			}
	    			
	            	//ターン中に敵か味方のいずれかが全滅している場合は、行動を終了させる。
	            	if( targetListEnemy.size() == 0 || targetListAlly.size() == 0 ) {
	            		break;
	            	}
	    			
	    			//単体攻撃処理
	    			if( enemyAction.getRange().equals( "single" )){
	    				//単体攻撃を処理
	    				AllyData allyData = enemyAction.attackSkillSingle( partyMap , targetListAlly );
	    				mesageList.add( enemyAction.getMessage() );
	    				mesageList.add( enemyAction.getBattleMessage() );
	    				if( enemyAction.getBuffMessage() != null ) {
	    					mesageList.add( enemyAction.getBuffMessage() );
	    				}
	    				if( allyData.getSurvival() == 0 ) {
	    					targetListAlly.remove( enemyAction.getTargetId() );
	    					targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    					mesageList.add( enemyAction.getResultMessage() );
	    				}else{
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    				}
	    			
	    			//全体攻撃を処理
	    			}else if( enemyAction.getRange().equals( "whole" )){
						mesageList.add( monsterData.getName() +  enemyAction.getMessage() );
						for( int j = 0 ; j < targetListAlly.size() ; j++ ) {
							int targetId = targetListAlly.get( j );
							AllyData allyData = enemyAction.attackSkillWhole( partyMap , targetId );
							if( enemyAction.getBattleMessage() != null ) {
								mesageList.add( enemyAction.getBattleMessage() );
							}
		    				if( enemyAction.getBuffMessage() != null ) {
		    					mesageList.add( enemyAction.getBuffMessage() );
		    				}
							if( allyData.getSurvival() == 0 ) {
								targetListAlly.remove( enemyAction.getTargetId() );
								targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
								partyMap.put( enemyAction.getTargetId() , allyData );
								mesageList.add( enemyAction.getResultMessage() );
							}else{
								partyMap.put( enemyAction.getTargetId() , allyData );
							}
						}
	
		    		//ミス系
		    		}else if( enemyAction.getPattern().equals( "miss" )){
		    			enemyAction.noAction();
		    			mesageList.add( monsterData.getName() + "の攻撃!!" );
		    			mesageList.add( "しかし、攻撃は外れてしまった…" );
		    			
		    		//死亡時など
		    		}else{
		    			enemyAction.noAction();
		    		}
    			}
    		}
        }
	}
	//-----------------------------------------------------------------------------------------------------------------------
	
	
	//攻撃魔法の処理メソッド
	public void attackMagicExecution( Integer key , Integer target , AllyData allyData , Action action ) {
		
		//攻撃対象者の情報を取得
		MonsterData monsterData = monsterDataMap.get( target );
		
		//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
		if( monsterData.getSurvival() == 0 ) {
			target = targetListEnemy.get( 0 );
			this.selectionMonsterMagic( key , target , targetMap.get( key ).getExecutionMagic() );
		}
		
		//攻撃処理後の対象者の情報を取得
		monsterData = action.actionAttackMagic( allyData , monsterDataMap.get( target )  , targetMap.get( key ).getExecutionMagic() );
		
		//攻撃対象者の生存状態によって処理を分岐（別メソッド化予定）
		if( monsterData.getCurrentHp() == 0 ) {
			monsterData.setSurvival( 0 );
			monsterDataMap.put( target , monsterData );
			mesageList.add( monsterData.getName() + "に" + action.getDamageMessage() );
			mesageList.add( monsterData.getName() + "を倒した!!" );
        	if( targetListEnemy.size() != 0 ) {
				target = targetListEnemy.get( 0 );
				this.selectionMonsterMagic( key , target , targetMap.get( key ).getExecutionMagic() );
        	}
		}else{
			monsterDataMap.put( target , monsterData );
			mesageList.add( monsterData.getName() + "に" + action.getDamageMessage() );
		}
	}
	
	//補助魔法の処理メソッド
	public void buffmagicExecution( Integer key , Integer target , AllyData allyData , Action action ) {
		AllyData receptionAllyData = action.actionBuffmagicMagic( allyData , partyMap.get( target ) , targetMap.get( key ).getExecutionMagic() );
		partyMap.put( target , receptionAllyData );
		mesageList.add( receptionAllyData.getName() + action.getBuffMessage() );
	}
	
	
	//蘇生魔法の処理メソッド
	public void resuscitationmagicExecution( Integer key , Integer target , AllyData allyData , Action action ) {
		AllyData receptionAllyData = action.actionResuscitationMagic( allyData , partyMap.get( target ) , targetMap.get( key ).getExecutionMagic() );
		partyMap.put( target , receptionAllyData );
		if( receptionAllyData.getSurvival() > 0 ) {
			targetListAlly.add( target );
		}
		mesageList.add( receptionAllyData.getName() + "は" + action.getRecoveryMessage() );
	}
	
	
	//妨害魔法の処理メソッド
	public void debuffMagicMagicExecution( Integer key , Integer target , AllyData allyData , Action action ) {
		
		//対象者の情報を取得
		MonsterData monsterData = monsterDataMap.get( target );
		
		//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
		if( monsterData.getSurvival() == 0 ) {
			target = targetListEnemy.get( 0 );
			this.selectionMonsterMagic( key , target , targetMap.get( key ).getExecutionMagic() );
		}
		//処理後の対象者の情報を取得
		monsterData = action.debuffMagicMagic( allyData , monsterDataMap.get( target )  , targetMap.get( key ).getExecutionMagic() );
		monsterDataMap.put( target , monsterData );
		mesageList.add( monsterData.getName() + "の" + action.getBuffMessage() );
	}
	
	
	//防御選択時の処理
	public void choiceDefense( AllyData allyData , Integer key ) {
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "正常" ) )
				.collect( Collectors.toSet() );
		
		statusSet.add( new Defense( allyData ) );
		allyData.setStatusSet( statusSet );
		partyMap.put( key , allyData );
	}
	
	
	//防御解除の処理
	public void cancelDefense( AllyData allyData , Integer key ) {
		Set<Status> statusSet = allyData.getStatusSet()
				.stream()
				.filter( s -> !s.getName().equals( "防御" ) )
				.collect( Collectors.toSet() );
		
		//状態異常中でなければ正常状態へ戻す。
		if( statusSet.size() == 0 ) {
			statusSet.add( new Normal() );
		}
		
		allyData.setStatusSet( statusSet );
		partyMap.put( key , allyData );
	}
	
	
	//行動不能系のステータス異常の処理（行動前処理）
	public Integer badStatusBefore( AllyData allyData , Integer key ) {
		
		//状態異常のメッセージ
		allyData.getStatusSet().stream()
		.filter( s -> !s.statusMessageBefore().equals( "no" ))
		.forEach( s -> this.mesageList.add( s.statusMessageBefore() ));
		
		//行動不能系のステータス異常の数を抽出（リストサイズが1以上なら行動ができない）
		List<Status> statusList = allyData.getStatusSet().stream()
		.filter( s -> s.actionStatusBefore() == 1 )
		.collect( Collectors.toList() );
		
		return statusList.size();
	}
	
	
	//ダメージ系（行動終了後に処理する状態異常のメソッド）
	public void badStatusAfter( AllyData allyData , Integer key ) {
		
		//ダメージ系の状態異常の処理
		List<Integer> damageList = new ArrayList<>();
		allyData.getStatusSet().stream()
		.forEach( s -> damageList.add( s.actionStatusAfter() ));
		
		//状態異常のメッセージ
		allyData.getStatusSet().stream()
		.filter( s -> !s.statusMessageAfter().equals( "no" ) )
		.forEach( s -> this.mesageList.add( s.statusMessageAfter() ));
		
		//自然治癒判定
		Set<Status> statusSet = allyData.getStatusSet().stream()
		.filter( s -> s.countDown() > 0 )
		.collect( Collectors.toSet() );
		
		//自然治癒メッセージをセット
		allyData.getStatusSet().stream()
		.filter( s -> s.getCount() == 0 )
		.filter( s -> !s.statusMessageAfter().equals( "no" ) )
		.forEach( s -> this.mesageList.add( s.recoverymessage() ));
		
		//状態異常がすべて完治した場合は、正常状態へ戻す。
		if( statusSet.size() == 0 ) {
			statusSet.add( new Normal() );
		}
		
		//聖なる守りの効果を元に戻す。
		if( statusSet.stream().filter( s -> s.getName().equals( "聖なる守り" )).count() == 0 ) {
			allyData.setSurvival( 1 );
		}
		
		//状態異常によるダメージを累計
		Integer result = damageList.stream().collect( Collectors.summingInt( s -> s ) );
		
		//ダメージ計算処理
		Integer HP = allyData.getCurrentHp() - result;
		
		//結果を反映
		if( HP <= 0 ) {
			allyData.setCurrentHp( 0 );
			allyData.setSurvival( 0 );
			statusSet.clear();
			statusSet.add( new Dead() );
			allyData.setStatusSet( statusSet );
			this.mesageList.add( allyData.getName() + "は死んでしまった…" );
			targetListAlly.remove( key );
			targetMap.put( key , new Target( key ) );
		}else{
			allyData.setCurrentHp( HP );
			allyData.setStatusSet( statusSet );
		}
		
		//結果を格納
		partyMap.put( key , allyData );
	}
	
}
