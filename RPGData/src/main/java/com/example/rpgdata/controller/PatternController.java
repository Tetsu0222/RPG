package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.entity.MonsterPattern;
import com.example.rpgdata.repository.MonsterPatternRepository;
import com.example.rpgdata.repository.MonsterRepository;
import com.example.rpgdata.support.MonsterPatternList;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PatternController {
	
	private final MonsterPatternRepository monsterPatternRepository;
	private final HttpSession session;
	private final MonsterRepository monsterRepository;
	
	//エネミーキャラクターの行動パターンの一覧画面に対応
	@GetMapping( "/edit/pattern" )
	public ModelAndView enemy( ModelAndView mv ) {
		
		mv.setViewName( "patternedit" );
		
		List<MonsterPattern> monsterPatternList = monsterPatternRepository.findAll();
		mv.addObject( "monsterPatternList" , monsterPatternList );
		session.setAttribute( "patternmode" , "edit" );
		
		return mv;
	}
	
	
    //エネミーキャラクターを選択→行動パターンの一覧に対応
    @GetMapping( "/enemy/pattern/{id}" )
    public ModelAndView magic( @PathVariable( name = "id" ) int id , 
    							ModelAndView mv ) {
    	
        mv.setViewName( "patternedit" );
        
        //選択されたキャラクターの情報を取得
        Monster monster = monsterRepository.findById( id ).orElseThrow();
		
        //キャラクターの行動パターンの一覧を格納するリストを生成
        List<MonsterPattern> monsterPatternList = MonsterPatternList.create( monster , monsterPatternRepository );
        
        //全行動パターンの抽出
        List<MonsterPattern> monsterPatternAllList = monsterPatternRepository.findAll();
        
		//全行動パターンのリストから追加可能な行動パターンだけを抽出
		List<MonsterPattern> monsterPatternAddPossibleList = MonsterPatternList.create( monsterPatternList , monsterPatternAllList );
		
        session.setAttribute( "patternmode" , "reading" );
        session.setAttribute( "monster" , monster );
        mv.addObject( "monsterPatternList" , monsterPatternList );
        mv.addObject( "monsterPatternAddPossibleList" , monsterPatternAddPossibleList );
        
		return mv;
		
    }
    
    
    //エネミーキャラクターの行動パターンを追加
    @PostMapping( "/enemy/pattern/add" )
    public String patternAdd( @RequestParam( name = "patternAddId" ) String patternAddId ,
    							Model model ) {
    	
    	//セッションからエネミーキャラクターの情報を取得
    	Monster monster = (Monster)session.getAttribute( "monster" );
    	
    	//行動パターンの追加処理
    	String pattern = MonsterPatternList.add( monster , patternAddId );
		
    	//追加された行動パターンを設定
    	monster.setPattern( pattern );
		
		//保存
    	monsterRepository.saveAndFlush( monster );
		
    	return "redirect:/enemy/pattern/" + monster.getId();
    	
    }
    
    
	//エネミーキャラクターの行動パターンを削除
	@PostMapping( "/enemy/pattern/delete/{id}" )
	public String enemyPatternDelete( @PathVariable( name = "id" ) String patternId ,
							  		Model model ) {
		
		//セッションからエネミーキャラクターの情報を取得
		Monster monster = (Monster)session.getAttribute( "monster" );
		
		//行動パターンの削除を実行
		String pattern = MonsterPatternList.delete( monster , patternId );
		
    	//追加された行動パターンを設定
    	monster.setPattern( pattern );
		
		//保存
		monsterRepository.saveAndFlush( monster );
		
		return "redirect:/enemy/pattern/" + monster.getId();
	}
    
}
