package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
	
}
