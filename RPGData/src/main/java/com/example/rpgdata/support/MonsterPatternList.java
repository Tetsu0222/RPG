package com.example.rpgdata.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.entity.MonsterPattern;
import com.example.rpgdata.repository.MonsterPatternRepository;

public class MonsterPatternList {
	

	//引数のエネミーキャラクターの行動パターンのリストを生成する。
	public static List<MonsterPattern> create( Monster Monster , MonsterPatternRepository monsterPatternRepository ) {
		
        //エネミーキャラクターの行動パターンのid一覧（無加工データ）を取得
        String pattern = Monster.getPattern() == null || Monster.getPattern().equals("") ? "3" : Monster.getPattern();
        
        //エネミーキャラクターの行動パターンを一覧で格納するリストを生成
        List<MonsterPattern> patternSourceList = new ArrayList<>();
        
		//行動パターンの一覧をid配列へ変換
		String[] patternSource = pattern.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( patternSource );
		
		//元の行動パターンそのものの削除などによる例外対策用のダミーオブジェクト
		MonsterPattern dummyPattern = monsterPatternRepository.findById( 3 ).orElseThrow();
				
		//idリストから行動パターンを検索していき、実際の行動パターンのリストへ格納していく。
		sourceList.stream()
					.map( s -> Integer.parseInt( s ) )
					.map( s ->  monsterPatternRepository.findById( s ) )
					.forEach( s -> patternSourceList.add( s.orElse( dummyPattern ) ));
		
		//実際の行動パターン一覧からダミーオブジェクトを除外する。
		List<MonsterPattern> patternList = patternSourceList.size() > 1 ? 
				patternSourceList.stream().filter( s -> s.getId() != 3 ).toList() : patternSourceList;
		
		return patternList;
	}
	
	
	//すべての行動パターンと設定されている行動パターンを突合、重複していない行動パターンだけを抽出する。
	public static List<MonsterPattern> create( List<MonsterPattern> monsterPatternList , List<MonsterPattern> monsterPatternAllListt ) {
		
		List<MonsterPattern>  monsterPatternAddPossibleList = monsterPatternAllListt.stream()
														.filter( s -> !monsterPatternList.contains( s ))
														.toList();
		
		if( monsterPatternAddPossibleList.size() == 0 ) {
			//追加可能な行動パターンがない時の処理
		}
		
		return monsterPatternAddPossibleList;
	}
	
	
	//行動パターンの追加に対応
	public static String add( Monster monster , String patternAddId ) {
		
		//プレイアブルキャラクターの現在の行動パターンを取得
    	String pattern = monster.getPattern();
    	
        //行動パターンがなければ、選択された行動パターンをそのまま追加
		if( pattern == null || pattern.equals("") ) {
			pattern = patternAddId;
		
		//使用可能な魔法に、選択された魔法を追加
		}else{
			
			//現在の行動パターンを配列に変換
			String[] patternSource = pattern.split( "," );
			
			//ソート可能なセットを生成
			Set<String> sourceSet = new TreeSet<>();
			
			//配列をセットへ変換
			Collections.addAll( sourceSet , patternSource );
			
			//セットに選択された行動パターンを追加
			sourceSet.add( patternAddId );
			
			//セットから行動パターンへ再変換
			pattern = sourceSet.stream().collect( Collectors.joining( "," ));
		}
		
		return pattern;
	}
	
	
	//エネミーキャラクターの行動パターンの削除に対応
	public static String delete( Monster monster , String patternId ) {
		
		//エネミーキャラクターの現在の行動パターンを取得
		String pattern = monster.getPattern();
		
		//行動パターンを配列に変換
		String[] patternSource = pattern.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( patternSource );
		
		//リストから削除指定された行動パターンを除く処理
		sourceList = sourceList.stream().filter( s -> !s.equals( patternId )).toList();
		
		//リストから行動パターンへ再変換
		pattern = sourceList.stream().collect( Collectors.joining( "," ));
		
		return pattern;
	}
	
}
