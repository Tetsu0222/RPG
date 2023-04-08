package com.example.rpg2.process;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.MonsterData;

public class IsDefense {
	
	
	//味方の防御状態チェック
	public static boolean isDefense( AllyData allyData ) {
		
		//対象者のステータス異常の中に防御が含まれているか確認(1が返れば含まれていると判定)
		Long i = allyData.getStatusSet()
				.stream()
				.filter( s -> s.getName().equals( "防御" ) )
				.count();
		
		//防御が含まれていればtureを返す。
		return i == 1;
	}
	
	
	//敵の防御状態チェック
	public static boolean isDefense( MonsterData monsterData ) {
		
		//対象者のステータス異常の中に防御が含まれているか確認(1が返れば含まれていると判定)
		Long i = monsterData.getStatusSet()
				.stream()
				.filter( s -> s.getName().equals( "防御" ) )
				.count();
		
		//防御が含まれていればtureを返す。
		return i == 1;
	}

}
