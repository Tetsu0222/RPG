package points;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
				actionEnemyCharacter.targetPlayerCharacterSelection( playerList.get( random.nextInt( playerList.size() )));
				actionEnemyCharacter.attak();
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
	
	public void create_turnQueue() {
		List< Integer > turnList = Stream.concat( playerkeylist.stream() , enemykeylist.stream() )
				.collect( Collectors.toList() );
		Collections.shuffle( turnList );
		
		turnList.stream().forEach( key -> turnqueue.add( key ));
	}
	
}
