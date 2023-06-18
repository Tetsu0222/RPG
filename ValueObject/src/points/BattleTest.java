package points;

import java.util.List;
import java.util.Random;

public class BattleTest {
	
	
	private List< PlayerCharacter > playerCharacterList;
	private List< EnemyCharacter > enemyCharacterList;
	private Random random = new Random();
	
	
	BattleTest( List< PlayerCharacter > playerCharacterList , List< EnemyCharacter > enemyCharacterList ){
		this.playerCharacterList = playerCharacterList;
		this.enemyCharacterList = enemyCharacterList;
	}
	
	
	public void battleStart() {
		
		while( playerCharacterList.size() > 0 && enemyCharacterList.size() > 0 ) {
			
			
			/*
			List< Characters > turnList = Stream.concat( playerCharacterList.stream() , enemyCharacterList.stream() )
	                						.collect( Collectors.toList() );
			
			*/
			
			playerCharacterList.stream().peek( pc -> pc.targetEnemyCharacterSelection( enemyCharacterList.get( random.nextInt( enemyCharacterList.size() ))))
										.peek( pc -> pc.attak() )
										.forEach( pc -> this.is_SurvivalList() );
			
			enemyCharacterList.stream().peek( pc -> pc.targetPlayerCharacterSelection( playerCharacterList.get( random.nextInt( playerCharacterList.size() ))))
			.peek( pc -> pc.attak() )
			.forEach( pc -> this.is_SurvivalList() );
			

			if( playerCharacterList.size() == 0 ) {
				System.out.println( "プレイヤーチームの負け" );
			}
			
			if( enemyCharacterList.size() == 0 ) {
				System.out.println( "プレイヤーチームの勝ち" );
			}
		}
	}
	
	
	//テスト項目
	public void is_SurvivalList() {
		this.playerCharacterList = playerCharacterList.stream().filter( pc -> pc.is_Survival() ).toList();
		this.enemyCharacterList = enemyCharacterList.stream().filter( pc -> pc.is_Survival() ).toList();
	}
	
	
	/*
				PlayerCharacter testA = playerCharacterList.get( 0 );
			PlayerCharacter testD = playerCharacterList.get( 1 );
			
			EnemyCharacter testB = enemyCharacterList.get( 0 );
			EnemyCharacter testC = enemyCharacterList.get( 1 );
			
			//プレイヤーキャラクターは攻撃対象を選択する。
			//選択した攻撃対象に通常攻撃を実施する。
			testA.targetEnemyCharacterSelection( enemyCharacterList.get( random.nextInt( enemyCharacterList.size() )));
			testA.attak();
			this.is_SurvivalList();
			
			testB.targetPlayerCharacterSelection( playerCharacterList.get( random.nextInt( enemyCharacterList.size() )));
			testB.attak();
			this.is_SurvivalList();
			
			testC.targetPlayerCharacterSelection( playerCharacterList.get( random.nextInt( enemyCharacterList.size() )));
			testC.attak();
			this.is_SurvivalList();
			
			testD.targetEnemyCharacterSelection( enemyCharacterList.get( random.nextInt( enemyCharacterList.size() ))  );
			testD.attak();
			this.is_SurvivalList();
	 */

}
