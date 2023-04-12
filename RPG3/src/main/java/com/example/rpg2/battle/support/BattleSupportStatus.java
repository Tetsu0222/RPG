package com.example.rpg2.battle.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.Battle;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.battle.Target;

import lombok.Data;

@Data
public class BattleSupportStatus {
	
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
	
	
	public BattleSupportStatus( Battle battle ) {
		this.partyMap = battle.getPartyMap();
		this.monsterDataMap = battle.getMonsterDataMap();
		this.targetMap = battle.getTargetMap();
		this.targetSetAlly = battle.getTargetSetAlly();
		this.targetSetEnemy = battle.getTargetSetEnemy();
		this.enemyNameList = battle.getEnemyNameList();
		this.allyNameList = battle.getAllyNameList();
	}

}
