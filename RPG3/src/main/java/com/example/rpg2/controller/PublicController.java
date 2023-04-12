package com.example.rpg2.controller;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpg2.battle.AllyData;
import com.example.rpg2.battle.Battle;
import com.example.rpg2.battle.MonsterData;
import com.example.rpg2.entity.Ally;
import com.example.rpg2.entity.Monster;
import com.example.rpg2.repository.AllyRepository;
import com.example.rpg2.repository.MagicRepository;
import com.example.rpg2.repository.MonsterPatternRepository;
import com.example.rpg2.repository.MonsterRepository;
import com.example.rpg2.repository.SkillRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PublicController {
	
	private final MagicRepository 			magicRepository;
	private final AllyRepository  			allyRepository;
	private final MonsterRepository 		monsterRepository;
	private final MonsterPatternRepository  monsterPatternRepository;
	private final HttpSession				session;
	private final SkillRepository			skillRepository;
	
	//行動する側の情報を管理
	private Integer myKeys;
	private Queue<Integer> turnqueue = new ArrayDeque<>();
	private List<String> allyNameList = new ArrayList<>();
	private List<String> enemyNameList = new ArrayList<>();
	
	private int turnCount = 1;
	private int actionObj;
	
	
	//TOP画面に対応
	@GetMapping( "/" )
	public ModelAndView Index( ModelAndView mv ) {
		
		mv.setViewName( "index" );
		
		//プレイアブルキャラクターとエネミーキャラクターの選択肢を提示
		List<Ally>    allyList    = allyRepository.findAll();
		List<Monster> monsterList = monsterRepository.findAll();

		mv.addObject( "allyList"    , allyList    );
		mv.addObject( "monsterList" , monsterList );
		
		session.invalidate();
		
		return mv;
	}
	
	
	//バトルへ遷移
	@GetMapping( "/battle" )
	public ModelAndView battle( @RequestParam( name = "PLV1" ) Integer pid1 ,
								@RequestParam( name = "PLV2" ) Integer pid2 ,
								@RequestParam( name = "PLV3" ) Integer pid3 ,
								@RequestParam( name = "PLV4" ) Integer pid4 ,
								@RequestParam( name = "MLV1" ) Integer mid1 ,
								@RequestParam( name = "MLV2" ) Integer mid2 ,
								@RequestParam( name = "MLV3" ) Integer mid3 ,
								@RequestParam( name = "MLV4" ) Integer mid4 ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		
		//名前区別用の配列とコレクション
		String abc[] = { "A" , "B" , "C" , "D" };
		List<String> nameList = new ArrayList<>();
		Set<String> duplicationNameSet = new HashSet<>();
				
		//選択に応じたプレイアブルキャラクターのIdを格納
		List<Integer> repositoryIdList = Stream.of( pid1 , pid2 , pid3 , pid4 )
				.filter( s -> s > 0 )
				.collect( Collectors.toList() );
		
		//生成プレイアブルキャラクターを格納するセット
		Set<AllyData> partySet = new HashSet<>();
		
		//プレイアブルキャラクターの生成
		for( int i = 0 ; i < repositoryIdList.size() ; i++ ) {
			Integer allyId = i;
			Integer repositoryId = repositoryIdList.get( i );
			AllyData allyData = new AllyData( allyRepository.findById( repositoryId ).orElseThrow() , magicRepository , skillRepository , allyId );
			String name = allyData.getName();
			
			//重複している名前か確認
			int count = (int)nameList.stream()
					.filter( s -> s.equals( name ))
					.count();
			
			//重複している名前であればセットへ格納
			if( count > 0 ) {
				duplicationNameSet.add( name );
			}
			
			//重複した名前か確認するリストへnameを格納
			nameList.add( name );
			
			//プレイアブルキャラクターのセットへ一時格納
			partySet.add( allyData );
		}
		
		//重複している名前の加工処理（ABCを付与)
		for( String name : duplicationNameSet ) {
			
			//プレイアブルキャラクターのセットを名前が重複しているオブジェクトに絞ったリストへ変換
			List<AllyData> duplicationNameList = partySet.stream()
					.filter( s -> s.getName().equals( name ) )
					.toList();
			
			//名前の加工処理
			for( int i = 0 ; i < duplicationNameList.size() ; i++ ) {
				AllyData allyData = duplicationNameList.get( i );
				
				//加工後の名前を設定
				allyData.setName( name + abc[i] );
				
				//プレイアブルキャラクターのセットへ再格納
				partySet.add( allyData );
			}
		}
		
		
		//選択に応じたエネミーオブジェクトを生成
		List<String> nameListEnemy = new ArrayList<>();
		Set<String> duplicationEnemyNameSet = new HashSet<>();
		
		//選択に応じたエネミーキャラクターのIdを格納
		List<Integer> repositoryEnemyIdList = Stream.of( mid1 , mid2 , mid3 , mid4 )
				.filter( s -> s > 0 )
				.collect( Collectors.toList() );
		
		//生成したエネミーキャラクターを格納するセット
		Set<MonsterData> monsterDataSet = new HashSet<>();
		
		//エネミーキャラクターの生成
		for( int i = 4 ; i < repositoryEnemyIdList.size() + 4 ; i++ ) {
			Integer enemyId = i;
			Integer repositoryEnemyId = repositoryEnemyIdList.get( i - 4  );
			MonsterData monsterData = new MonsterData( monsterRepository.findById( repositoryEnemyId ).orElseThrow() , monsterPatternRepository , enemyId );
			String name = monsterData.getName();
			monsterData.setOriginalName( name );
			
			//重複している名前か確認
			int count = (int)nameListEnemy.stream()
					.filter( s -> s.equals( name ))
					.count();
			
			//重複している名前であればセットへ格納
			if( count > 0 ) {
				duplicationEnemyNameSet.add( name );
			}
			
			//重複した名前か確認するリストへnameを格納
			nameListEnemy.add( name );
			
			//エネミーキャラクターのセットへ一時格納
			monsterDataSet.add( monsterData );
		}
		
		//重複している名前の加工処理（ABCを付与)
		for( String name : duplicationEnemyNameSet ) {
			
			//エネミーキャラクターのセットを名前が重複しているオブジェクトに絞ったリストへ変換
			List<MonsterData> duplicationEnemyNameList = monsterDataSet.stream()
					.filter( s -> s.getName().equals( name ) )
					.toList();
			
			//名前の加工処理
			for( int i = 0 ; i < duplicationEnemyNameList.size() ; i++ ) {
				MonsterData monsterData = duplicationEnemyNameList.get( i );
				
				//加工後の名前を設定
				monsterData.setName( name + abc[i] );
				
				//エネミーキャラクターのセットへ再格納
				monsterDataSet.add( monsterData );
			}
		}
		
		//グループ攻撃用の重複要素を整理したリスト生成（順番を維持したいためリストにて生成）
		allyNameList  = nameList.stream().distinct().toList();
		enemyNameList = nameListEnemy.stream().distinct().toList();
	
		//戦闘処理用のオブジェクトを生成
		Battle battle = new Battle( partySet , monsterDataSet , allyNameList , enemyNameList );
		battle.createSupport();
		
		//戦闘画面用のデータをセッションスコープに保存
		session.setAttribute( "battle" , battle );
		
		return mv;
	}
	
	
	//通常攻撃を選択
	@GetMapping( "/attack/{key}" )
	public ModelAndView attack( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		myKeys = key;
		session.setAttribute( "mode" , "attackTargetMonster" );
		
		return mv;
		
	}
	
	
	//通常攻撃のターゲット選択(敵）
	@GetMapping( "/target/attack/monster/{key}" )
	public ModelAndView attackTargetMonster( @PathVariable( name = "key" ) int key ,
											 ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionAttack( myKeys , key );
		
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}
	
	
	//防御を選択
	@GetMapping( "/defense/{key}" )
	public ModelAndView defense( @PathVariable( name = "key" ) int key ,
								 ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionDefense( key );
		
		return mv;
	}
	
	
	//戦闘開始
	@GetMapping( "/start" )
	public ModelAndView start( ModelAndView mv ) {
		
		//いつもの処理
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		//前回までのログを消去
		battle.getMesageList().clear();
		
		//各キャラクターの行動順を規定
		battle.turn();
		
		List<Entry<Integer, Integer>> turnList = battle.getTurnList();
		
		for( Entry<Integer, Integer> entry : turnList) {
			Integer key = entry.getKey();
			turnqueue.add( key );
		}
		
		//ターンの最初に発動する効果を処理
		battle.startSkill();
		battle.getMesageList().add( turnCount + "ターン目開始" );
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode"   , "battle" );

		return mv;
	}
	
	
	//戦闘続行
	@GetMapping( "/next" )
	public ModelAndView next( ModelAndView mv ) {
		
		//いつもの処理
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
	
		//前回までのログを消去
		battle.getMesageList().clear();
		
		//素早さ順に行動
		if( turnqueue.peek() != null ) {
			
			this.actionObj = turnqueue.poll();
			
			//ターン終了判定
			if( this.isPossible( battle )){
				
				//判定結果trueであれば行動実行
				battle.startBattle( this.actionObj );
				
				//戦闘終了判定
				if( battle.getTargetSetAlly().size() == 0 ) {
					session.invalidate();
					battle.getMesageList().add( "全滅してしまった…" );
					session.setAttribute( "battle" , battle );
					session.setAttribute( "mode" , "result" );
					
				}else if( battle.getTargetSetEnemy().size() == 0 ) {
					session.invalidate();
					battle.getMesageList().add( "戦いに勝利した!!!" );
					session.setAttribute( "battle" , battle );
					session.setAttribute( "mode" , "result" );
					
				}else{
					session.invalidate();
					session.setAttribute( "battle" , battle );
					session.setAttribute( "mode" , "battle" );
				}
			
			//全員の行動が終了
			}else{
				
				//ターン終了時に発動する処理
				battle.endSkill();
				battle.getMesageList().add( turnCount + "ターン目終了" );
				this.turnCount += 1;
				
				session.invalidate();
				session.setAttribute( "battle" , battle );
				session.setAttribute( "mode"   , "end"  );
			}
			
		//全キャラクターの行動終了
		}else{
			
			//ターン終了時に発動する処理
			battle.endSkill();
			battle.getMesageList().add( turnCount + "ターン目終了" );
			this.turnCount += 1;
			
			session.invalidate();
			session.setAttribute( "battle" , battle );
			session.setAttribute( "mode"   , "end"  );
		}
		
		return mv;
	}
	
	
	//ターン終了
	@GetMapping( "/end" )
	public ModelAndView end( ModelAndView mv ) {
		
		//いつもの処理
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		session.invalidate();
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}
	
	
	//ターンを続行するか判定するメソッド、falseを返すとターン終了させる。
	public boolean isPossible( Battle battle ) {
		
		boolean possible = false;
		
		//味方側の生存チェック
		if( battle.getPartyMap().get( this.actionObj ) != null ){
			
			//生存しているかどうかで処理を分岐
			if( battle.getPartyMap().get( this.actionObj ).getSurvival() == 0 ) {
				
				//行動対象者が死亡している場合は、該当インデックスを次の行動対象者で上書き
				if( turnqueue.peek() != null ) {
					this.actionObj = turnqueue.poll();
					
					//次の行動対象者も生存チェックを実行
					if( this.isPossible( battle )) {
						possible = true;
					
					//自メソッドを繰り返し、結果的に値がなくなっていればターン終了判定(false)を返す。
					}else{
						possible = false;
					}
				
				//次の値が存在しなければターン終了(falseを返す)
				}else{
					possible = false;
				}
				
			//生存していれば処理実行
			}else{
				possible = true;
			}
			
		//敵側の生存チェック
		}else if( battle.getMonsterDataMap().get( actionObj ) != null ){
			
			if( battle.getMonsterDataMap().get( actionObj ).getSurvival() == 0 ) {
					
				//行動対象者が死亡している場合は、該当インデックスを次の行動対象者で上書き
				if( turnqueue.peek() != null ) {
					this.actionObj = turnqueue.poll();
						
					//次の行動対象者も生存チェックを実行
					if( this.isPossible( battle )) {
						possible = true;
						
					//自メソッドを繰り返し、結果的に値がなくなっていればターン終了判定(false)を返す。
					}else{
						possible = false;
					}
					
				//次の値が存在しなければターン終了(falseを返す)
				}else{
					possible = false;
				}
					
			//生存していれば処理実行
			}else{
				possible = true;
			}
		}
		
		return possible;
	}
	
}
