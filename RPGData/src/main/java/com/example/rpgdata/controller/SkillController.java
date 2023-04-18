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
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Skill;
import com.example.rpgdata.form.SkillForm;
import com.example.rpgdata.repository.AllyRepository;
import com.example.rpgdata.repository.SkillRepository;
import com.example.rpgdata.support.SkillList;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SkillController {

	private final AllyRepository allyRepository;
	private final SkillRepository skillRepository;
	private final HttpSession session;
	
	
	
	
    //特技の一覧に対応
    @GetMapping("/edit/skill")
    public ModelAndView skillEdit( ModelAndView mv ) {
    	
        mv.setViewName( "skill" );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Skill> skillAllList = SkillList.create( skillRepository );
		
		session.setAttribute( "announcement" , "normal" );
        session.setAttribute( "skillmode" , "edit" );
        mv.addObject( "skillAllList" , skillAllList );
        
		return mv;
		
    }
	
	
    //プレイアブルキャラクターの使用可能な特技一覧に対応
    @GetMapping("/ally/skill/{id}")
    public ModelAndView skill( @PathVariable( name = "id" ) int id , 
    							ModelAndView mv ) {
    	
        mv.setViewName( "skill" );
        
        //選択されたキャラクターの情報を取得
        Ally ally = allyRepository.findById( id ).orElseThrow();
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Skill> skillList = SkillList.create( ally , skillRepository );
        
		//ダミー魔法を除いた全魔法リストを生成
        List<Skill> skillAllList = SkillList.create( skillRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
        List<Skill> skillAddPossibleList = SkillList.create( skillList , skillAllList );
		
        session.setAttribute( "skillmode" , "reading" );
        session.setAttribute( "ally" , ally );
        mv.addObject( "skillList" , skillList );
        mv.addObject( "skillAllList" , skillAddPossibleList );
        
		return mv;
		
    }
    
    
    //プレイアブルキャラクターの新規作成→特技登録に対応
    @PostMapping("/ally/skill")
    public ModelAndView skill2( ModelAndView mv ) {
    	
        mv.setViewName( "skill" );
        
    	//セッションからプレイアブルキャラクターの情報を取得
    	Ally ally = (Ally)session.getAttribute( "ally" );
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Skill> skillList = SkillList.create( ally , skillRepository );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Skill> skillAllList = SkillList.create( skillRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
		List<Skill> skillAddPossibleList = SkillList.create( skillList , skillAllList );
		
        session.setAttribute( "skillmode" , "reading" );
        session.setAttribute( "ally" , ally );
        mv.addObject( "skillList" , skillList );
        mv.addObject( "skillAllList" , skillAddPossibleList );
        
		return mv;
		
    }
    
    
    //プレイアブルキャラクターの使用可能な特技を追加
    @PostMapping("/ally/skill/add")
    public String skillAdd( @RequestParam( name = "skillAddId" ) String skillAddId ,
    						Model model ) {
    	
    	//セッションからプレイアブルキャラクターの情報を取得
    	Ally ally = (Ally)session.getAttribute( "ally" );
    	
    	//魔法の追加処理
    	String skill = SkillList.add( ally , skillAddId );
		
    	//追加された魔法を設定
		ally.setSkill( skill );
		
		//保存
		allyRepository.saveAndFlush( ally );
		
    	return "redirect:/ally/skill/" + ally.getId();
    }
    
    
	//キャラクターの使用可能な特技を削除
	@PostMapping( "/ally/skill/delete/{id}" )
	public String allySkillDelete( @PathVariable( name = "id" ) String skillId ,
							  		Model model ) {
		
		//セッションからプレイアブルキャラクターの情報を取得
		Ally ally = (Ally)session.getAttribute( "ally" );
		
		//特技の削除を実行
		String skill = SkillList.delete( ally , skillId );
		
		//削除後の特技を設定
		ally.setSkill( skill );
		
		//保存
		allyRepository.saveAndFlush( ally );
		
		return "redirect:/ally/skill/" + ally.getId();
	}
	
	
	//特技の新規登録のボタンに対応
	@GetMapping( "/skill/create" )
	public ModelAndView skillCreate( ModelAndView mv ) {
		
		mv.setViewName( "skillcreate" );
		mv.addObject( "skillForm" , new SkillForm() );
		session.setAttribute( "mode" , "skillcreate" );
		
		return mv;
		
	}
	
	
	//特技の新規登録
	@PostMapping( "/skill/create" )
	public String skillCreateDo( @ModelAttribute @Validated SkillForm skillForm ,
									BindingResult result ,
									Model model ) {
		
		//エラーなし
		if( !result.hasErrors() ) {
			Skill skill = skillForm.toEntity();
			skillRepository.saveAndFlush( skill );
			session.setAttribute( "announcement" , "success" );
			
			return "redirect:/skill/create";
			
		//エラーあり	
		}else{
			return "skillcreate";
		}
		
	}
	
	
}
