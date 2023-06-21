package points;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Random;

public class BattleTestMap {
	
	
	private Map< Integer , PlayerCharacter > playerCharacterMap;
	private Map< Integer , EnemyCharacter > enemyCharacterMap;
	private List< Integer > playerkeylist;
	private List< PlayerCharacter > playerList;
	private List< Integer > enemykeylist;
	private List< EnemyCharacter > enemyList;
	
	private Queue< Integer > turnqueue = new ArrayDeque<>();
	private Random random = new Random();
	private int turnCount = 1;
	private final String turnStartMessage = "ターン目開始";
	private final String turnEndMessage = "ターン目終了";
	private final String resultMessage_WIN = "プレイヤーチームの勝ち";
	private final String resultMessage_LOW = "プレイヤーチームの負け";
	private final Integer Max_playerCharacterMap_Key = 3;
	private final Integer Min_enemyCharacterMap_key = 4;
	
	
	BattleTestMap( Map< Integer , PlayerCharacter > playerCharacterMap , Map< Integer , EnemyCharacter > enemyCharacterMap ){
		this.playerCharacterMap = playerCharacterMap;
		this.enemyCharacterMap = enemyCharacterMap;
		this.playerkeylist = new ArrayList<>( playerCharacterMap.keySet() );
		this.playerList = new ArrayList<>( playerCharacterMap.values() );
		this.enemykeylist = new ArrayList<>( enemyCharacterMap.keySet() );
		this.enemyList = new ArrayList<>( enemyCharacterMap.values() );
	}
	
	public void battleStart() {
		
		this.create_turnQueue();
		System.out.println( turnCount + turnStartMessage );
		
		while( playerList.size() > 0 && enemyList.size() > 0 ) {
			
			if( turnqueue.peek() == null ) {
				System.out.println( turnCount + turnEndMessage );
				System.out.println();
				System.out.println();
				System.out.println();
				turnCount++;
				System.out.println( turnCount + turnStartMessage );
				this.create_turnQueue();
			}
			
			
			final Integer actionKey = turnqueue.poll();
			
			
			if( actionKey < Min_enemyCharacterMap_key ) {
				PlayerCharacter actionPlayerCharacter = playerCharacterMap.get( actionKey );
				actionPlayerCharacter.targetEnemyCharacterSelection( enemyList.get( random.nextInt( enemyList.size() )));
				actionPlayerCharacter.attak();
				this.is_SurvivalList();
			}
			
			if( actionKey > Max_playerCharacterMap_Key ) {
				EnemyCharacter actionEnemyCharacter = enemyCharacterMap.get( actionKey );
				actionEnemyCharacter.targetSelection( playerList );
				this.is_SurvivalList();
			}
			
			if( playerList.size() == 0 ) {
				System.out.println( resultMessage_LOW );
			}
			
			if( enemyList.size() == 0 ) {
				System.out.println( resultMessage_WIN );
			}
		}
	}
	
	
	//テスト項目
	public void is_SurvivalList() {
		this.playerList = playerList.stream().filter( pc -> pc.is_Survival() ).toList();
		this.enemyList = enemyList.stream().filter( pc -> pc.is_Survival() ).toList();
	}
	

	//キャラクターの素早さで順番を生成
	//戦闘不能のキャラクターの素早さ取得を行わないようにリファクタリング予定
	//各キャラクターの素早さと素早さの取得方法もリファクタリング予定
	public void create_turnQueue() {
		
		//各キャラの座標と素早さで構成されたマップ
		Map<Integer,Integer> turnMap = new HashMap<>();
		
		//味方の座標と素早さをマップへ格納
		for( Integer index : playerkeylist ) {
			
			PlayerCharacter player = playerCharacterMap.get( index );
			
			if( !player.is_Survival() ) {
				continue;
			}
			
			Integer spe = player.getSPD();
			
			turnMap.put( index , spe );
		}
		
		//敵の座標と素早さをマップへ格納
		for( Integer index : enemykeylist ) {
			
			EnemyCharacter enemy = enemyCharacterMap.get( index );
			
			if( !enemy.is_Survival() ) {
				continue;
			}
			
			Integer spe = enemy.getSPD();
			
			turnMap.put( index , spe );
		}
		
		//敵味方の混合マップからエントリーを抽出、各キャラの座標が素早さの高い順（降順）でソートされているリストを生成
		List<Entry<Integer, Integer>> turnList = new ArrayList<Entry<Integer, Integer>>( turnMap.entrySet() );
        Collections.sort( turnList , new Comparator<Entry<Integer, Integer>>() {
            public int compare( Entry<Integer, Integer> obj1 , Entry<Integer, Integer> obj2 )
            {
            	return obj2.getValue().compareTo( obj1.getValue() );
            }
        });
        
		for( Entry<Integer, Integer> entry : turnList){
			Integer key = entry.getKey();
			this.turnqueue.add( key );
		}
	}
	
}
