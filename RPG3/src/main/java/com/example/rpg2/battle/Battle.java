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
import com.example.rpg2.action.BuffMagic;
import com.example.rpg2.action.DeBuffMagic;
import com.example.rpg2.action.MagicAttack;
import com.example.rpg2.action.RecoveryMagic;
import com.example.rpg2.action.TaregetEnemyAction;
import com.example.rpg2.action.TargetAllyAction;
import com.example.rpg2.entity.Magic;
import com.example.rpg2.status.Dead;
import com.example.rpg2.status.Defense;
import com.example.rpg2.status.Normal;
import com.example.rpg2.status.Status;

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
		this.targetSetEnemy = new TreeSet<>( monsterDataMap.keySet() );
		this.targetSetAlly  = new TreeSet<>( partyMap.keySet() );
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
		Target target = new Target ( partyMap , targetSetAlly , myKeys ,  magic , 1 );
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
		Target target = new Target( monsterDataMap , targetSetEnemy , myKeys ,  magic );
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
					
					//通常攻撃を生成
					TaregetEnemyAction at = new Attack( allyData );
					
					//通常攻撃を実施
					this.mesageList.add( at.getStratMessage() );
					this.singleAttack( at,  target , key );
					
				//回復魔法の処理
				}else if( movementPattern.equals( "recoverymagic" )) {
					
					//回復魔法を生成
					TargetAllyAction recoveryMagic = new RecoveryMagic( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( recoveryMagic.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( recoveryMagic.isNotEnoughMp() ){
						this.mesageList.add( recoveryMagic.getNotEnoughMpMessage() );
					
					//MP判定OK
					}else{
						//全体回復魔法の処理
						if( targetMap.get( key ).getTargetSetAlly() != null ) {
							this.generalSupport( recoveryMagic, key );
						//単体回復魔法の処理
						}else{
							this.singleSupport( recoveryMagic , target , key );
						}
					}
				
					
				//攻撃魔法の処理
				}else if( movementPattern.equals( "attackmagic" )) {
					
					//攻撃魔法を生成
					TaregetEnemyAction magicAttack = new MagicAttack( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( magicAttack.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( magicAttack.isNotEnoughMp() ){
						this.mesageList.add( magicAttack.getNotEnoughMpMessage() );
						
					//MP判定OK
					}else{
						
						//全体攻撃魔法の処理
						if( targetMap.get( key ).getTargetSetEnemy() != null ) {
							this.generalAttack( magicAttack , key );
							
						//単体攻撃魔法の処理
						}else{
							this.singleAttack( magicAttack , target , key );
						}
					}
				
					
				//補助魔法の処理
				}else if( movementPattern.equals( "buffmagic" )) {
					
					//補助魔法を生成
					TargetAllyAction buffMagic = new BuffMagic( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( buffMagic.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( buffMagic.isNotEnoughMp() ){
						this.mesageList.add( buffMagic.getNotEnoughMpMessage() );
					
						//MP判定OK
						}else{
							//全体回復魔法の処理
							if( targetMap.get( key ).getTargetSetAlly() != null ) {
								this.generalSupport( buffMagic, key );
							//単体回復魔法の処理
							}else{
								this.singleSupport( buffMagic , target , key );
							}
						}
					
					
				//蘇生魔法の処理
				}else if( movementPattern.equals( "resuscitationmagic" )) {
					
					//蘇生魔法を生成(回復魔法と同一オブジェクトにて処理)
					TargetAllyAction recoveryMagic = new RecoveryMagic( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( recoveryMagic.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( recoveryMagic.isNotEnoughMp() ){
						this.mesageList.add( recoveryMagic.getNotEnoughMpMessage() );
					
					//MP判定OK
					}else{
						
						//全体蘇生魔法の処理
						if( targetMap.get( key ).getTargetSetEnemy() != null ) {
							this.resuscitationMagicExecution( recoveryMagic , -1 , key );
							
						//単体蘇生魔法の処理
						}else{
							this.resuscitationMagicExecution( recoveryMagic , target , key );
						}
					}
					
					
				//妨害の処理(攻撃魔法の処理と合体させる予定)
				}else if( movementPattern.equals( "debuffmagic" )) {
					
					//妨害魔法を生成
					TaregetEnemyAction deBuffMagic = new DeBuffMagic( allyData , targetMap.get( key ).getExecutionMagic()  );
					this.mesageList.add( deBuffMagic.getStratMessage() );
					
					//MP判定 MPが足りないとtureが返る。
					if( deBuffMagic.isNotEnoughMp() ){
						this.mesageList.add( deBuffMagic.getNotEnoughMpMessage() );
						
					//MP判定OK
					}else{
						
						//全体妨害魔法の処理
						if( targetMap.get( key ).getTargetSetEnemy() != null ) {
							this.generalAttack( deBuffMagic , key );
							
						//単体妨害魔法の処理
						}else{
							this.singleAttack( deBuffMagic , target , key );
						}
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
	            	if( targetSetEnemy.size() == 0 || targetSetAlly.size() == 0 ) {
	            		break;
	            	}
	    			
	    			//単体攻撃処理
	    			if( enemyAction.getRange().equals( "single" )){
	    				//単体攻撃を処理
	    				AllyData allyData = enemyAction.attackSkillSingle( partyMap , targetSetAlly );
	    				mesageList.add( enemyAction.getMessage() );
	    				mesageList.add( enemyAction.getBattleMessage() );
	    				if( enemyAction.getBuffMessage() != null ) {
	    					mesageList.add( enemyAction.getBuffMessage() );
	    				}
	    				if( allyData.getSurvival() == 0 ) {
	    					targetSetAlly.remove( enemyAction.getTargetId() );
	    					targetMap.put( enemyAction.getTargetId() , new Target( enemyAction.getTargetId() ) );
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    					mesageList.add( enemyAction.getResultMessage() );
	    				}else{
	    					partyMap.put( enemyAction.getTargetId() , allyData );
	    				}
	    			
	    			//全体攻撃を処理
	    			}else if( enemyAction.getRange().equals( "whole" )){
	    				
	    				List<Integer> targetList = new ArrayList<Integer>( targetSetAlly );
	    				
						mesageList.add( monsterData.getName() +  enemyAction.getMessage() );
						for( int j = 0 ; j < targetList.size() ; j++ ) {
							int targetId = targetList.get( j );
							AllyData allyData = enemyAction.attackSkillWhole( partyMap , targetId );
							if( enemyAction.getBattleMessage() != null ) {
								mesageList.add( enemyAction.getBattleMessage() );
							}
		    				if( enemyAction.getBuffMessage() != null ) {
		    					mesageList.add( enemyAction.getBuffMessage() );
		    				}
							if( allyData.getSurvival() == 0 ) {
								targetSetAlly.remove( enemyAction.getTargetId() );
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
	
	
	//単体攻撃のメソッド
	public void singleAttack( TaregetEnemyAction taregetEnemyAction , Integer target , Integer key ) {
		
		//攻撃対象のオブジェクトを取得
		MonsterData monsterData = monsterDataMap.get( target );
		
		//対象がターン中に死亡している場合は、別の生存対象へ処理対象を変更
		if( monsterData.getSurvival() == 0 ) {
			target = targetSetEnemy.stream().findAny().orElse( 0 );
			this.selectionMonsterMagic( key , target , targetMap.get( key ).getExecutionMagic() );
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
			targetSetEnemy.remove( target );
        	if( targetSetEnemy.size() != 0 ) {
        		target = targetSetEnemy.stream().findAny().orElseThrow();
				this.selectionAttack( key , target );
        	}
		}
		
		//MP消費処理
		if( targetMap.get( key ).getExecutionMagic() != null ) {
			this.consumptionMP( key );
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
		this.mesageList.add( targetAllyAction.getRecoveryMessage() );
		
		//MP消費処理
		if( targetMap.get( key ).getExecutionMagic() != null ) {
				this.consumptionMP( key );
		}
		
	}
	
	
	//全体攻撃のメソッド
	public void generalAttack( TaregetEnemyAction taregetEnemyAction , Integer key ) {
		
		//targetを敵全体へ変更
		for( Integer target : targetSetEnemy ) {
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
		
		//MP消費処理
		if( targetMap.get( key ).getExecutionMagic() != null ) {
				this.consumptionMP( key );
		}
	}
	
	
	//全体回復・補助のメソッド
	public void generalSupport( TargetAllyAction targetAllyAction , Integer key) {
		
		//ターゲットを全体に変更
		for( Integer target : targetSetAlly ) {
			AllyData receptionAllyData = targetAllyAction.action( partyMap.get( target ) );
			partyMap.put( target , receptionAllyData );
			this.mesageList.add( targetAllyAction.getRecoveryMessage() );
		}
		
		//MP消費処理
		if( targetMap.get( key ).getExecutionMagic() != null ) {
				this.consumptionMP( key );
		}
		
	}
	
	//蘇生魔法の処理メソッド
	public void resuscitationMagicExecution( TargetAllyAction targetAllyAction , Integer target , Integer key ) {
		
		//全体魔法の処理
		if( target < 0 ) {
			
			//ターゲットを全体で再設定
			for( Integer target2 : targetSetAlly ) {
				
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
		
		//MP消費処理
		if( targetMap.get( key ).getExecutionMagic() != null ) {
				this.consumptionMP( key );
		}
		
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
			targetSetAlly.remove( key );
			targetMap.put( key , new Target( key ) );
		}else{
			allyData.setCurrentHp( HP );
			allyData.setStatusSet( statusSet );
		}
		
		//結果を格納
		partyMap.put( key , allyData );
	}
	
	
	//MP消費処理
	public void consumptionMP( Integer key ) {
		AllyData allyData = partyMap.get( key );
		int MP = allyData.getCurrentMp();
		MP -= targetMap.get( key ).getExecutionMagic().getMp();
		allyData.setCurrentMp( MP );
		partyMap.put( key , allyData );
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
	
}
