package com.example.rpgdata.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
}
