package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.form.MonsterForm;
import com.example.rpgdata.repository.MonsterRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MonsterController {
	
	private final MonsterRepository monsterRepository;
	private final HttpSession session;
	
	
	
	//エネミーキャラクターの一覧画面に対応
	@GetMapping( "/edit/enemy" )
	public ModelAndView enemy( ModelAndView mv ) {
		
		mv.setViewName( "enemyedit" );
        
		List<Monster> monsterList = monsterRepository.findAll();
		mv.addObject( "monsterList", monsterList );
		
		return mv;
	}
	
	
	//エネミーキャラクター新規登録のボタンに対応
	@GetMapping( "/enemy/create" )
	public ModelAndView enemyCreate( ModelAndView mv ) {
		
		List<Monster> monsterList = monsterRepository.findAll();
		
		mv.setViewName( "monstercreate" );
		mv.addObject( "monsterForm" , new MonsterForm() );
		
		session.setAttribute( "monsterList" , monsterList );
		session.setAttribute( "mode" , "create" );
		
		return mv;
		
	}
	
	
	//エネミーキャラクター新規登録に対応
	@PostMapping( "/enemy/create" )
	public String enemyCreate( @ModelAttribute @Validated MonsterForm monsterForm ,
								BindingResult result ,
								Model model ) {
		
		//エラーなし
		if( !result.hasErrors() ) {
			Monster monster = monsterForm.toEntity();
			monster = monsterRepository.saveAndFlush( monster );
			session.setAttribute( "monster" , monster );
			session.setAttribute( "announcement" , "monster" );
			
			return "redirect:/enemy/create";
			
		//エラーあり	
		}else{
			return "monstercreate";
		}
	}
	
	
	//戻るボタンに対応
	@PostMapping( "/enemy/cancel" )
	public String cancel( Model model ) {
		
		session.setAttribute( "announcement" , "normal" );
		session.setAttribute( "mode" , "normal" );
		
		return "redirect:/edit/ally";
	}
	
}
