package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.MonsterPattern;
import com.example.rpgdata.repository.MonsterPatternRepository;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PatternController {
	
	private final MonsterPatternRepository monsterPatternRepository;
	private final HttpSession session;

	
	//エネミーキャラクターの行動パターンの一覧画面に対応
	@GetMapping( "/edit/pattern" )
	public ModelAndView enemy( ModelAndView mv ) {
		
		mv.setViewName( "patternedit" );
		List<MonsterPattern> monsterPatternList = monsterPatternRepository.findAll();
		mv.addObject( "monsterPatternList" , monsterPatternList );
		
		return mv;
	}
}
