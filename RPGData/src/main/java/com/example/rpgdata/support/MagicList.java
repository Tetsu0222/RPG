package com.example.rpgdata.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.repository.MagicRepository;

public class MagicList {
	
	
	//引数のプレイアブルキャラクターが使用可能な魔法のリストを生成する。
	public static List<Magic> create( Ally ally , MagicRepository magicRepository ) {
		
        //キャラクターの魔法id一覧（無加工データ）を取得
        String magic = ally.getMagic();
        
        //使用可能な魔法がなければ、ダミー魔法を生成
		if( magic == null || magic.equals("") ) {
			magic = "26";
		}
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Magic> magicList = new ArrayList<>();
        
		//魔法一覧をid配列へ変換
		String[] magicSource = magic.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( magicSource );
		
		//idリストから魔法を検索していき、使用可能な魔法一覧リストへ格納していく。
		sourceList.stream()
					.map( s -> Integer.parseInt( s ) )
					.map( s ->  magicRepository.findById( s ) )
					.forEach( s -> magicList.add( s.orElseThrow() ));
		
		return magicList;
	}
	
	
	//ダミー魔法を除いたすべての魔法のリストを生成する。
	public static List<Magic> create( MagicRepository magicRepository ) {
		
		List<Magic> magicAllList = magicRepository.findAll().stream()
				.filter( s -> s.getId() != 26 )
				.toList();
		
		return magicAllList;
	}
	
	
	//すべての魔法リストと使用可能な魔法リストで重複している魔法を除外する（追加可能な魔法リストを生成）
	public static List<Magic> create( List<Magic> magicList , List<Magic> magicAllList ) {
		
		List<Magic> magicAddPossibleList = magicAllList.stream()
				.filter( s -> !magicList.contains( s ))
				.toList();
		
		if( magicAddPossibleList.size() == 0 ) {
			//追加可能な魔法がない時の処理
		}
		
		
		return magicAddPossibleList;
	}
	
	
	//使用可能な魔法の追加に対応
	public static String add( Ally ally , String magicAddId ) {
		
		//プレイアブルキャラクターの今の魔法を取得
    	String magic = ally.getMagic();
    	
        //使用可能な魔法がなければ、選択された魔法をそのまま追加
		if( magic == null || magic.equals("") ) {
			magic = magicAddId;
		
		//使用可能な魔法に、選択された魔法を追加
		}else{
			
			//今の魔法を配列に変換
			String[] magicSource = magic.split( "," );
			
			//ソート可能なセットを生成
			Set<String> sourceSet = new TreeSet<>();
			
			//配列をセットへ変換
			Collections.addAll( sourceSet , magicSource );
			
			//セットに選択された魔法を追加
			sourceSet.add( magicAddId );
			
			//セットから魔法へ再変換
			magic = sourceSet.stream().collect( Collectors.joining( "," ));
		}
		
		return magic;
	}
	
	
	//使用可能な魔法の削除に対応
	public static String delete( Ally ally , String magicId ) {
		
		//プレイアブルキャラクターの今の魔法を取得
		String magic = ally.getMagic();
		
		//魔法を配列に変換
		String[] magicSource = magic.split( "," );
		
		//配列をリストへ変換
		List<String> sourceList = Arrays.asList( magicSource );
		
		//リストから削除指定された魔法を除く処理
		sourceList = sourceList.stream().filter( s -> !s.equals( magicId )).toList();
		
		//リストから魔法へ再変換
		magic = sourceList.stream().collect( Collectors.joining( "," ));
		
		return magic;
	}

}
