package com.example.rpg2.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpg2.battle.Battle;
import com.example.rpg2.entity.Skill;
import com.example.rpg2.repository.SkillRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;



@Controller
@RequiredArgsConstructor
public class SkillController {
	
	private final HttpSession				session;
	private final SkillRepository			skillRepository;

	//行動する側の情報を管理
	private Integer myKeys;
	private Skill skill;
	
	
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
	
	
	//攻撃特技の選択画面を表示
	@GetMapping( "/skill/attack/{key}" )
	public ModelAndView magicA( @PathVariable( name = "key" ) int key ,
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		
		myKeys = key;
		
		//発動可能な特技一覧を表示
		List<Skill> skillList = battle.getPartyMap().get( key ).getSkillList();
		List<Skill> skillListA = skillList.stream()
				.filter( s -> s.getCategory().equals( "targetenemy" ))
				.filter( s -> s.getBuffcategory().equals( "no" ) )
				.collect( Collectors.toList() );

		mv.addObject( "skillList" , skillListA );
		mv.addObject( "key" , myKeys );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
		
	}
	
	
	//特技を選択
	@GetMapping( "/skill/add/{id}" )
	public ModelAndView skill2( @PathVariable( name = "id" ) int id , 
								ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		skill = skillRepository.findById( id ).get();
		
		//単体魔法かつ攻撃魔法と妨害魔法以外→対象選択の範囲を味方に指定
		if( skill.getRange().equals( "single" ) && skill.getCategory().equals( "targetally" ) || skill.getCategory().equals( "resuscitationskill" ) ) {
			
			//蘇生特技以外
			if( !skill.getCategory().equals( "resuscitationskill" )) {
				session.setAttribute( "mode" , "targetAllySkill" );
				
			//蘇生特技
			}else{
				session.setAttribute( "mode" , "targetDeathAllySkill" );
			}
			
		//単体特技かつ攻撃・妨害特技→対象選択の範囲を敵に指定
		}else if( skill.getRange().equals( "single" ) && skill.getCategory().equals( "targetenemy" ) ) {
			session.setAttribute( "mode" , "targetMonsterSkill" );
		
		//味方全体への特技
		}else if( !skill.getRange().equals( "single" ) && skill.getCategory().equals( "targetally" ) ) {
			battle.selectionAllySkill( myKeys , skill );
			session.setAttribute( "mode" , "log" );
		
		//敵全体への特技
		}else{
			battle.selectionMonsterSkill( myKeys , skill );
			session.setAttribute( "mode" , "log" );
		}
		
		session.setAttribute( "battle" , battle );
		
		return mv;
		
	}
	
	//ターゲット選択(味方への特技）
	@GetMapping( "/target/skill/ally/{key}" )
	public ModelAndView targetAlly( @PathVariable( name = "key" ) int key ,
									ModelAndView mv ) {
		
		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionAllySkill( myKeys , key , skill );
		
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}
	
	
	//ターゲット選択(攻撃特技)
	@GetMapping( "/target/skill/monster/{key}" )
	public ModelAndView skillTargetMonster( @PathVariable( name = "key" ) int key ,
											ModelAndView mv ) {

		mv.setViewName( "battle" );
		Battle battle = (Battle)session.getAttribute( "battle" );
		battle.selectionMonsterSkill( myKeys , key , skill );
		
		session.setAttribute( "battle" , battle );
		session.setAttribute( "mode" , "log" );
		
		return mv;
	}

}
