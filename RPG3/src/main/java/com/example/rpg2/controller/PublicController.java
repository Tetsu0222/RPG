package com.example.rpg2.controller;

import java.util.ArrayList;
import java.util.List;
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
import com.example.rpg2.entity.Magic;
import com.example.rpg2.entity.Monster;
import com.example.rpg2.entity.Skill;
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
	private Magic   magic;
	
	
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
		
		//選択に応じたプレイアブルキャラクターを生成
		List<AllyData> partyList = new ArrayList<>();
		Stream.of( pid1 , pid2 ,pid3 , pid4 )
		.filter( s -> s > 0 )
		.map( s -> allyRepository.findById( s ).orElseThrow() )
		.map( s -> new AllyData( s , magicRepository , skillRepository ))
		.forEach( s -> partyList.add( s ) );
		
		
		//選択に応じたエネミーオブジェクトを生成
		List<MonsterData> monsterDataList = new ArrayList<>();
		Stream.of( mid1 , mid2 , mid3 , mid4)
		.filter( s -> s > 0 )
		.map( s -> monsterRepository.findById( s ).orElseThrow() )
		.map( s -> new MonsterData( s , monsterPatternRepository ))
		.forEach( s -> monsterDataList.add( s ) );
		
		//戦闘処理用のオブジェクトを生成
		Battle battle = new Battle( partyList , monsterDataList );
		
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
	
	
	//すべての魔法の選択画面を表示
	@GetMapping( "/magic/{key}" )
	public ModelAndView magic( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Magic> magicList = battle.getPartyMap().get( key ).getMagicList();
		mv.addObject( "magicList" , magicList );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	
	//攻撃魔法の選択画面を表示
	@GetMapping( "/magic/attack/{key}" )
	public ModelAndView magicA( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Magic> magicList = battle.getPartyMap().get( key ).getMagicList();
		List<Magic> magicListA = magicList.stream()
				.filter( s -> s.getCategory().equals( "targetenemy" ))
				.filter( s -> s.getBuffcategory().equals( "no" ) )
				.collect( Collectors.toList() );

		mv.addObject( "magicList" , magicListA );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	//回復魔法の選択画面を表示
	@GetMapping( "/magic/recovery/{key}" )
	public ModelAndView magicR( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Magic> magicList = battle.getPartyMap().get( key ).getMagicList();
		List<Magic> magicListR = magicList.stream()
				.filter( s -> s.getCategory().equals( "targetally" ) || s.getCategory().equals( "resuscitationmagic" ) )
				.filter( s -> s.getBuffcategory().equals( "no" ) )
				.collect( Collectors.toList() );

		mv.addObject( "magicList" , magicListR );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	//補助魔法の選択画面を表示
	@GetMapping( "/magic/buff/{key}" )
	public ModelAndView magicB( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Magic> magicList = battle.getPartyMap().get( key ).getMagicList();
		List<Magic> magicListB = magicList.stream()
				.filter( s -> s.getCategory().equals( "targetally" ) )
				.filter( s -> !s.getBuffcategory().equals( "no" ) )
				.collect( Collectors.toList() );

		mv.addObject( "magicList" , magicListB );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	
	//妨害魔法の選択画面を表示
	@GetMapping( "/magic/debuff/{key}" )
	public ModelAndView magicD( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Magic> magicList = battle.getPartyMap().get( key ).getMagicList();
		List<Magic> magicListA = magicList.stream()
				.filter( s -> s.getCategory().equals( "targetenemy" ))
				.filter( s -> !s.getBuffcategory().equals( "no" ) )
				.collect( Collectors.toList() );

		mv.addObject( "magicList" , magicListA );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	
	//魔法を選択
	@GetMapping( "/magic/add/{id}" )
	public ModelAndView magic2( @PathVariable( name = "id" ) int id , 
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		magic = magicRepository.findById( id ).get();
		
		//単体魔法かつ攻撃魔法と妨害魔法以外→対象選択の範囲を味方に指定
		if( magic.getRange().equals( "single" ) && magic.getCategory().equals( "targetally" ) || magic.getCategory().equals( "resuscitationmagic" ) ) {
			
			//蘇生魔法以外
			if( !magic.getCategory().equals( "resuscitationmagic" )) {
				session.setAttribute( "mode" , "targetAllyMagic" );
				
			//蘇生魔法
			}else{
				session.setAttribute( "mode" , "targetDeathAllyMagic" );
			}
			
		//単体魔法かつ攻撃・妨害魔法→対象選択の範囲を敵に指定
		}else if( magic.getRange().equals( "single" ) && magic.getCategory().equals( "targetenemy" ) ) {
			session.setAttribute( "mode" , "targetMonsterMagic" );
		
		//味方全体への魔法
		}else if( !magic.getRange().equals( "single" ) && magic.getCategory().equals( "targetally" ) ) {
			battle.selectionAllyMagic( myKeys , magic );
			session.setAttribute( "mode" , "log" );
		
		//敵全体への魔法
		}else{
			battle.selectionMonsterMagic( myKeys , magic );
			session.setAttribute( "mode" , "log" );
		}
		
		session.setAttribute( "battle" , battle );
		
		return mv;
		
	}
	
	
	//ターゲット選択(味方への魔法）
	@GetMapping( "/target/magic/ally/{key}" )
	public ModelAndView targetAlly( @PathVariable( name = "key" ) int key ,
									ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionAllyMagic( myKeys , key , magic );
		
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}
	
	
	//ターゲット選択(攻撃魔法)
	@GetMapping( "/target/magic/monster/{key}" )
	public ModelAndView magicTargetMonster( @PathVariable( name = "key" ) int key ,
											ModelAndView mv ) {

		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionMonsterMagic( myKeys , key , magic );
		
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}
	
	
	//すべての特技の選択画面を表示
	@GetMapping( "/skill/{key}" )
	public ModelAndView skill( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な魔法一覧を表示
		List<Skill> skillList = battle.getPartyMap().get( key ).getSkillList();
		mv.addObject( "skillList" , skillList );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "skill" );
		
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
		
		//戦闘開始
		battle.startBattle();
		
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
			session.setAttribute( "mode" , "log" );
		}

		return mv;
	}
	

}
