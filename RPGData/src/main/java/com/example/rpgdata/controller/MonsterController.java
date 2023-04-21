package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.form.MonsterForm;
import com.example.rpgdata.repository.AttachedFileRepository;
import com.example.rpgdata.repository.MonsterRepository;
import com.example.rpgdata.support.SaveAttachedFile;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MonsterController {
	
	private final MonsterRepository monsterRepository;
	private final HttpSession session;
	private final AttachedFileRepository attachedFileRepository;
	
	
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
	
	
	//エネミーキャラクターの名前押下に対応→更新画面へ遷移
    @GetMapping("/enemy/{id}")
    public ModelAndView enemyById( @PathVariable( name = "id" ) int id , 
    								ModelAndView mv ) {
    	
        mv.setViewName( "monstercreate" );
        Monster monster = monsterRepository.findById( id ).orElseThrow();
        
        mv.addObject( "monsterForm" , monster );
        session.setAttribute( "mode" , "update" );
        
        return mv;
    }
    
    
    //更新に対応
    @PostMapping( "/enemy/update" )
    public String update( @ModelAttribute @Validated MonsterForm monsterForm ,
    						BindingResult result ,
    						Model model ) {
    	
		//エラーなし
		if( !result.hasErrors() ) {
			Monster monster = monsterForm.toEntity();
			monsterRepository.saveAndFlush( monster );
			session.setAttribute( "announcement" , "success" );
			
			return "redirect:/enemy/" + monsterForm.getId() ;
			
		//エラーあり	
		}else{
			return "monstercreate";
		}
    }
	
	
	//エネミーキャラクター削除に対応
	@PostMapping( "/enemy/delete/{id}" )
	public String enemyDelete( @PathVariable( name = "id" ) int id ,
							  Model model ) {
		
		monsterRepository.deleteById( id );
		
		return "redirect:/edit/enemy";
	}
	
	
	//戻るボタンに対応
	@PostMapping( "/enemy/cancel" )
	public String cancel( Model model ) {
		
		session.setAttribute( "announcement" , "normal" );
		session.setAttribute( "mode" , "normal" );
		
		return "redirect:/edit/enemy";
	}
	
	
	//行動パターンの一覧画面からエネミーキャラクターの詳細画面へ推移（戻るボタン対応）
    @PostMapping("/enemy/{id}")
    public ModelAndView enemyById2( @PathVariable( name = "id" ) int id , 
    								ModelAndView mv ) {
    	
        mv.setViewName( "monstercreate" );
        Monster monster = monsterRepository.findById( id ).orElseThrow();
        
        mv.addObject( "monsterForm" , monster );
        session.setAttribute( "mode" , "update" );
        
        return mv;
    }
    
    
	//新規作成→登録完了に対応
	@PostMapping( "/enemy/complete" )
	public String complete( Model model ) {
		
		session.invalidate();
		
		return "redirect:/edit/enemy";
	}
	
	
	//新規作成→登録完了に対応
	@GetMapping( "/enemy/complete" )
	public String complete2( Model model ) {
		
		session.invalidate();
		
		return "redirect:/edit/enemy";
	}
	
	
	//エネミーキャラクターの画像をアップロード（工事中）
	@PostMapping("/enemy/photo/upload")
	public String uploadAttachedFile( @RequestParam( "enemy_id" ) int enemyId,
										@RequestParam( "note" ) String note,
										@RequestParam( "file_contents" ) MultipartFile fileContents ) {
		
		//ファイルが空かチェック
		if( fileContents.isEmpty() ) {
			//no
			
		}else{
			SaveAttachedFile saveAttachedFile = new SaveAttachedFile();
			saveAttachedFile.saveAttachedFile( enemyId , note , fileContents , attachedFileRepository );
		}
		
		
		return "redirect:/edit/enemy";
    }
	
}
