package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Monster;
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
	
	
	//エネミーキャラクターの行動パターン一覧を確認
	@GetMapping( "/enemy/pattern/{id}" )
    public ModelAndView magic( @PathVariable( name = "id" ) int id , 
								ModelAndView mv ) {

		mv.setViewName( "pattern" );
		
		//選択されたキャラクターの情報を取得
		Monster monster = monsterRepository.findById( id ).orElseThrow();

		mv.addObject( "monsterList", monster );
		
		
		return mv;
	}
}
