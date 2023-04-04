package com.example.rpg2.battle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.example.rpg2.entity.Magic;

import lombok.Data;

@Data
public class Battle {
	
	
	//プレイアブルメンバーを管理
	Map<Integer,AllyData> partyMap;
	
	//エネミーメンバーを管理
	Map<Integer,MonsterData> monsterDataMap;
	
	//プレイアブルメンバーの行動選択を管理
	Map<Integer,Target> targetMap;
	
	//味方の数とキーを管理
	List<Integer> targetListAlly;
	
	//敵の数とキーを管理
	List<Integer> targetListEnemy;
	
	//表示するログを管理
	List<String> mesageList = new ArrayList<>();
	
	//行動順を規定
	List<Entry<Integer, Integer>> turnList;
	
	//防御選択者
	List<Integer> defenseList = new ArrayList<>();
	
	
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
	
	
	//通常攻撃を選択
	public void selectionAttack( Integer myKeys ,  Integer key ) {

		Target target = new Target( monsterDataMap.get( key ) , myKeys , key );
		targetMap.put( myKeys , target );
	}
	
	
	//味方への魔法を選択
	public void selectionAllyMagic( Integer myKeys , Integer key , Magic magic ) {
		
		Target target = new Target ( partyMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
	}
	
	
	//味方への全体魔法を選択
	public void selectionAllyMagic( Integer myKeys , Magic magic ) {
		
		//最後の引数はオーバーロード用のダミー
		Target target = new Target ( partyMap , targetListAlly , myKeys ,  magic , 1 );
		targetMap.put( myKeys , target );
	}
	
	
	//敵への魔法を選択
	public void selectionMonsterMagic( Integer myKeys , Integer key , Magic magic ) {
		
		Target target = new Target( monsterDataMap.get( key ) , myKeys , key , magic );
		targetMap.put( myKeys , target );
	}
	
	
	//敵への全体魔法を選択
	public void selectionMonsterMagic( Integer myKeys , Magic magic ) {
		
		Target target = new Target( monsterDataMap , targetListEnemy , myKeys ,  magic );
		targetMap.put( myKeys , target );
	}
	
	
	//防御を選択
	public void selectionDefense( Integer myKeys ) {
		Target target = new Target( myKeys , "防御" );
		targetMap.put( myKeys , target );
		this.defenseList.add( myKeys );
	}
	
	
	//防御行動のリセット
	public void selectionDefenseAfter() {
		
		for( int i = 0 ; i < defenseList.size(); i++ ) {
			int index = defenseList.get( i );
			AllyData allyData = partyMap.get( index );
			Integer def = allyData.getCurrentDEF();
			if( def > 0 ) {
				def = def / 2 ;
			}
			allyData.setCurrentDEF( def );
		}
		defenseList.clear();
	}

	
	//行動順を決定
	public void turn() {
		Map<Integer,Integer> turnMap = new HashMap<>();
		Random random = new Random();
		
		for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
			Integer index = targetListAlly.get( i );
			Integer spe   = partyMap.get( index ).getCurrentSPE();
			spe += random.nextInt( spe / 2 );
			turnMap.put( index , spe );
		}
		
		for( int i = 0 ; i < targetListEnemy.size() ; i++ ) {
			Integer index = targetListEnemy.get( i );
			Integer spe   = monsterDataMap.get( index ).getCurrentSPE();
			spe += random.nextInt( spe / 2 );
			turnMap.put( index , spe );
		}
		
		//行動順になるようにソート(素早さの数値で降順に並べる)
		this.turnList = new ArrayList<Entry<Integer, Integer>>( turnMap.entrySet() );
        Collections.sort( turnList , new Comparator<Entry<Integer, Integer>>() {
            public int compare( Entry<Integer, Integer> obj1 , Entry<Integer, Integer> obj2 )
            {
            	return obj2.getValue().compareTo( obj1.getValue() );
            }
        });
	}
	
	
	//戦闘開始
	public void startBattle() {
		
		//防御選択者の処理(内部計算は暫定処理）
		if( defenseList.size() > 0 ) {
			for( int i = 0 ; i < defenseList.size(); i++ ) {
				int index = defenseList.get( i );
				AllyData allyData = partyMap.get( index );
				Integer def = allyData.getCurrentDEF();
				if( def == 0 ) {
					def = 10;
				}else {
					def *= 2;
				}
				allyData.setCurrentDEF( def );
			}
		}
		
		//各行動処理
        for( Entry<Integer, Integer> entry : turnList ) {
        	
        	//ターン中に敵か味方のいずれかが全滅している場合は、戦闘を終了させる。
        	if( targetListEnemy.size() == 0 || targetListAlly.size() == 0 ) {
        		break;
        	}
        	
            int key = entry.getKey();
            
            //味方側の処理
            if( partyMap.get( key ) != null ) {
    			Action   action   = new Action();
    			AllyData allyData = partyMap.get( key );
    			Integer  target	  = targetMap.get( key ).getSelectionId();
    			String   movementPattern = targetMap.get( key ).getCategory();
    			
    			//通常攻撃の処理
				if( movementPattern.equals( "attack" )) {
					MonsterData monsterData = monsterDataMap.get( target );
					
					//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
					if( monsterData.getSurvival() == 0 ) {
						target = targetListEnemy.get( 0 );
						this.selectionAttack( key , target );
					}
					
					monsterData = action.actionAttack( allyData , monsterDataMap.get( target ) );
					mesageList.add( allyData.getName() + "の攻撃!!!" );
					
					//行動結果の処理
					if( monsterData.getCurrentHp() == 0 ) {
						monsterData.setSurvival( 0 );
						targetListEnemy.remove( target );
						monsterDataMap.put( target , monsterData );
						mesageList.add( monsterData.getName() + "に" + action.getDamageMessage() );
						mesageList.add( monsterData.getName() + "を倒した!!" );
			        	if( targetListEnemy.size() != 0 ) {
							target = targetListEnemy.get( 0 );
							this.selectionAttack( key , target );
			        	}
					}else{
						monsterDataMap.put( target , monsterData );
						mesageList.add( monsterData.getName() + "に" + action.getDamageMessage() );
					}
					
				//回復魔法の処理
				}else if( movementPattern.equals( "recoverymagic" )) {
					mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
					//MP判定処理
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						//全体回復魔法の処理
						if( targetMap.get( key ).getTargetListAlly() != null ) {
							for( int i = 0 ; i < targetListAlly.size() ; i++ ) {
								target = targetListAlly.get( i );
								this.recoverymagicExecution( key , target , allyData , action );
							}
						//単体回復魔法の処理
						}else{
							//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
							if( allyData.getSurvival() == 0 ) {
								target = targetListAlly.get( 0 );
								this.selectionAllyMagic( key , target , targetMap.get( key ).getExecutionMagic() );
							}
							this.recoverymagicExecution( key , target , allyData , action );
						}
					//MP消費処理（別メソッド化予定）
					allyData = action.consumptionMp( allyData , targetMap.get( key ).getExecutionMagic() );
					partyMap.put( key , allyData );
					}
				
				//攻撃魔法の処理
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
				
				//補助魔法の処理
				}else if( movementPattern.equals( "buffmagic" )) {
					
					//MP判定処理
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
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
					
				//蘇生魔法の処理
				}else if( movementPattern.equals( "resuscitationmagic" )) {
					
					//MP判定処理
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
						mesageList.add( "しかしMPが足りない･･･" );
					}else{
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
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
					
				//妨害の処理
				}else if( movementPattern.equals( "debuffmagic" )) {
					
					//MP判定
					if( targetMap.get( key ).getExecutionMagic().getMp() > allyData.getCurrentMp() ) {
						action.noAction();
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
						mesageList.add( "しかしMPが足りない･･･" );
						
					}else{
						mesageList.add( allyData.getName() + "は" + targetMap.get( key ).getSkillName() + "を放った!!" );
						
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
				
			//敵側の行動処理
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
	    			if( enemyAction.getRange().equals( "single" )) {
	    				//単体攻撃を処理
	    				AllyData allyData = enemyAction.attackSkillSingle( partyMap , targetListAlly );
	    				mesageList.add( enemyAction.getMessage() );
	    				if( allyData.getSurvival() == 0 ) {
	    					targetListAlly.remove( enemyAction.getTargetId() );
	    					targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    					mesageList.add( allyData.getName() + "に" + enemyAction.getDamage() + "のダメージ!!!" );
	    					mesageList.add( allyData.getName() + "は死んでしまった…" );
	    				}else{
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    					mesageList.add( allyData.getName() + "に" + enemyAction.getDamage() + "のダメージ!!!" );
	    				}
	    			
	    			//全体攻撃を処理
	    			}else if( enemyAction.getRange().equals( "whole" )){
						mesageList.add( monsterData.getName() +  enemyAction.getMessage() );
						for( int j = 0 ; j < targetListAlly.size() ; j++ ) {
							int targetId = targetListAlly.get( j );
							AllyData allyData = enemyAction.attackSkillWhole( partyMap , targetId );
							if( allyData.getCurrentHp() == 0 ) {
								targetListAlly.remove( enemyAction.getTargetId() );
								targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
								partyMap.put( enemyAction.getTargetId() , allyData );
								mesageList.add( allyData.getName() + "に" + enemyAction.getDamage() + "のダメージ!!!" );
								mesageList.add( allyData.getName() + "は死んでしまった…" );
							}else{
								partyMap.put( enemyAction.getTargetId() , allyData );
								mesageList.add( allyData.getName() + "に" + enemyAction.getDamage() + "のダメージ!!!" );
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
	
	
	//回復魔法の処理メソッド
	public void recoverymagicExecution( Integer key , Integer target , AllyData allyData , Action action ) {
		AllyData receptionAllyData = action.actionRecoveryMagic( allyData , partyMap.get( target ) , targetMap.get( key ).getExecutionMagic() );
		partyMap.put( target , receptionAllyData );
		mesageList.add( receptionAllyData.getName() + "は" + action.getRecoveryMessage() );
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
	
}
