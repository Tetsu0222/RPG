package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.entity.MonsterPattern;
import com.example.rpgdata.entity.Skill;
import com.example.rpgdata.form.AllyForm;
import com.example.rpgdata.repository.AllyRepository;
import com.example.rpgdata.repository.MagicRepository;
import com.example.rpgdata.repository.MonsterPatternRepository;
import com.example.rpgdata.repository.MonsterRepository;
import com.example.rpgdata.repository.SkillRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PublicController {
	
	private final MagicRepository 			magicRepository;
	private final AllyRepository  			allyRepository;
	private final MonsterRepository 		monsterRepository;
	private final MonsterPatternRepository  monsterPatternRepository;
	private final SkillRepository			skillRepository;
	private final HttpSession				session;
	
	
	//TOP画面に対応
	@GetMapping( "/" )
	public ModelAndView Index( ModelAndView mv ) {
		
		mv.setViewName( "index" );
		return mv;
	}
	
	
	//味方側のキャラクター編集に対応
	@GetMapping( "/edit/ally" )
	public ModelAndView ally( ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		List<Ally> allyList = allyRepository.findAll();
		mv.addObject( "allyList" , allyList );
		mv.addObject( "allyForm" , new AllyForm() );
		session.setAttribute( "mode" , "ally" );
		
		return mv;
	}
	
	
	//味方側のキャラクター新規登録に対応
	@PostMapping( "/ally/create" )
	public String allyCreate( @ModelAttribute AllyForm allyForm ,
								Model model ) {
		
		Ally ally = allyForm.toEntity();
		allyRepository.saveAndFlush( ally );
		
		return "redirect:/edit/ally";
	}
	
	
	//味方側のキャラクター削除に対応
	@PostMapping( "/ally/delete/{id}" )
	public String allyDelete( @PathVariable( name = "id" ) int id ,
							  Model model ) {
		
		allyRepository.deleteById( id );
		
		return "redirect:/edit/ally";
	}
	
	
	//敵側のキャラクター編集に対応
	@GetMapping( "/edit/monster" )
	public ModelAndView monster( ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		List<Monster> monsterList = monsterRepository.findAll();
		mv.addObject( "monsterList" , monsterList );
		session.setAttribute( "mode" , "monster" );
		
		return mv;
	}
	
	
	//魔法の編集に対応
	@GetMapping( "/edit/magic" )
	public ModelAndView magic( ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		List<Magic> magicList = magicRepository.findAll();
		mv.addObject( "magicList" , magicList );
		session.setAttribute( "mode" , "magic" );
		
		return mv;
	}
	
	
	//特技の編集に対応
	@GetMapping( "/edit/skill" )
	public ModelAndView skill( ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		List<Skill> skillList = skillRepository.findAll();
		mv.addObject( "skillList" , skillList );
		session.setAttribute( "mode" , "skill" );
		
		return mv;
	}
	
	
	//敵の行動の編集に対応
	@GetMapping( "/edit/monsterPattern" )
	public ModelAndView monsterPattern( ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		List<MonsterPattern> monsterPatternList = monsterPatternRepository.findAll();
		mv.addObject( "monsterPatternList" , monsterPatternList );
		session.setAttribute( "mode" , "monsterPattern" );
		
		return mv;
	}
	
}
