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


public class TurnOrderCreate {
	
	public static Queue<Integer> create( Map< Integer , PlayerCharacter > playerCharacterMap ,
														Map< Integer , EnemyCharacter > enemyCharacterMap ,
														List< Integer > playerkeylist ,
														List< Integer > enemykeylist ) {
		
		//各キャラの座標と素早さで構成されたマップ
		Map<Integer,Integer> turnMap = new HashMap<>();
		
		
		//味方の座標と素早さをマップへ格納
		for( Integer index : playerkeylist ) {
			Integer spe = playerCharacterMap.get( index ).getSPD();
			
			turnMap.put( index , spe );
		}
		
		//敵の座標と素早さをマップへ格納
		for( Integer index : enemykeylist ) {
			Integer spe   = enemyCharacterMap.get( index ).getSPD();
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
        
        Queue<Integer> turnqueue = new ArrayDeque<>();
        
		for( Entry<Integer, Integer> entry : turnList){
			Integer key = entry.getKey();
			turnqueue.add( key );
		}
        
        return turnqueue;
	}
	
}
