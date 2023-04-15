package com.example.rpgdata.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Skill;
import com.example.rpgdata.repository.SkillRepository;

public class SkillList {

	//引数のプレイアブルキャラクターが使用可能な特技のリストを生成する。
	public static List<Skill> create( Ally ally , SkillRepository skillRepository ) {
		
        //キャラクターの特技id一覧（無加工データ）を取得
        String skill = ally.getSkill();
        
        //使用可能な特技がなければ、ダミー特技を生成
		if( skill == null || skill.equals("") ) {
			skill = "2";
		}
		
        //キャラクターの使用可能な特技を一覧で格納するリストを生成
        List<Skill> skillList = new ArrayList<>();
        
		//特技一覧をid配列へ変換
		String[] skillSource = skill.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( skillSource );
		
		//idリストから特技を検索していき、使用可能な特技一覧リストへ格納していく。
		sourceList.stream()
					.map( s -> Integer.parseInt( s ) )
					.map( s ->  skillRepository.findById( s ) )
					.forEach( s -> skillList.add( s.orElseThrow() ));
		
		return skillList;
	}
	
	
	//ダミー特技を除いたすべての特技のリストを生成する。
	public static List<Skill> create( SkillRepository skillRepository ) {
		
		List<Skill> skillAllList = skillRepository.findAll().stream()
				.filter( s -> s.getId() != 2 )
				.toList();
		
		return skillAllList;
	}
	
	
	//すべての特技リストと使用可能な特技リストで重複している特技を除外する（追加可能な特技リストを生成）
	public static List<Skill> create( List<Skill> skillList , List<Skill> skillAllList ) {
		
		List<Skill> skillAddPossibleList = skillAllList.stream()
				.filter( s -> !skillList.contains( s ))
				.toList();

		return skillAddPossibleList;
	}
	
	
	//使用可能な特技の追加に対応
	public static String add( Ally ally , String skillId ) {
		
		//プレイアブルキャラクターの今の特技を取得
    	String skill = ally.getSkill();
    	
        //使用可能な特技がなければ、選択された特技をそのまま追加
		if( skill == null || skill.equals("") ) {
			skill = skillId;
		
		//使用可能な特技に、選択された特技を追加
		}else{
			
			//今の特技を配列に変換
			String[] skillSource = skill.split( "," );
			
			//ソート可能なセットを生成
			Set<String> sourceSet = new TreeSet<>();
			
			//配列をセットへ変換
			Collections.addAll( sourceSet , skillSource );
			
			//セットに選択された特技を追加
			sourceSet.add( skillId );
			
			//セットから特技へ再変換
			skill = sourceSet.stream().collect( Collectors.joining( "," ));
		}
		
		return skill;
	}
	
	
	//特技の削除に対応
	public static String delete( Ally ally , String skillId ) {
		
		//プレイアブルキャラクターの今の魔法を取得
		String skill = ally.getSkill();
		
		//魔法を配列に変換
		String[] skillSource = skill.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( skillSource );
		
		//リストから削除指定された魔法を除く処理
		sourceList = sourceList.stream().filter( s -> !s.equals( skillId )).toList();
		
		//リストから魔法へ再変換
		skill = sourceList.stream().collect( Collectors.joining( "," ));
		
		return skill;
	}
}
