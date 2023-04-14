package com.example.rpgdata.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import com.example.rpgdata.dao.AllyDaoImp;
import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.entity.Monster;
import com.example.rpgdata.entity.MonsterPattern;
import com.example.rpgdata.entity.Skill;
import com.example.rpgdata.form.AllyForm;
import com.example.rpgdata.query.AllyQuery;
import com.example.rpgdata.repository.AllyRepository;
import com.example.rpgdata.repository.MagicRepository;
import com.example.rpgdata.repository.MonsterPatternRepository;
import com.example.rpgdata.repository.MonsterRepository;
import com.example.rpgdata.repository.SkillRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
	
	@PersistenceContext
	private EntityManager entityManager;
	private AllyDaoImp allyDaoImp;
	
	@PostConstruct
	public void init() {
		allyDaoImp = new AllyDaoImp( entityManager );
	}
	
	
	//TOP画面に対応
	@GetMapping( "/" )
	public ModelAndView Index( ModelAndView mv ) {
		
		mv.setViewName( "index" );
		return mv;
	}
	
	
	//味方側のキャラクター編集に対応
	@GetMapping( "/edit/ally" )
	public ModelAndView ally( ModelAndView mv ,
							  @PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ) {
		
		mv.setViewName( "edit" );
		
		//前回までの検索状態をセッションから取得
		AllyQuery allyQuery = (AllyQuery)session.getAttribute( "allyQuery" );
		
		//セッションに情報がなければ新規生成
        if( allyQuery == null ) {
        	allyQuery = new AllyQuery();
            session.setAttribute( "allyQuery" , allyQuery );
        }
        
        //前回までのページ設定をセッションから取得
        Pageable prevPageable = (Pageable)session.getAttribute( "prevPageable" );
        
        //セッションに情報がなければ新規生成
        if( prevPageable == null ) {
        	prevPageable = pageable;
        	session.setAttribute( "prevPageable" , prevPageable );
        }
        
		Page<Ally> pageList = allyDaoImp.findByCriteria( allyQuery , prevPageable );
		session.setAttribute( "allyList" , pageList.getContent() );
		session.setAttribute( "mode" , "ally" );
		mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "allyPage", pageList );
		
		return mv;
	}
	
	
	//ページリンクの押下に対応
    @GetMapping("/ally/query")
    public ModelAndView queryTodo( @PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
                                   ModelAndView mv) {
    	
    	mv.setViewName( "edit" );
    	
        //現在のページ位置を保存
        session.setAttribute( "prevPageable" , pageable );
        
        //sessionに保存されている条件で検索
        AllyQuery allyQuery = (AllyQuery)session.getAttribute( "allyQuery" );
        Page<Ally> pageList = allyDaoImp.findByCriteria( allyQuery , pageable );
        
        session.setAttribute( "mode" , "ally" );
        session.setAttribute( "allyList" , pageList.getContent() );
        session.setAttribute( "allyQuery" , allyQuery );
        
        mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "allyPage", pageList );

        return mv;
    }
	
	
	//味方側のキャラクター新規登録に対応
	@PostMapping( "/ally/create" )
	public String allyCreate( @ModelAttribute @Validated AllyForm allyForm ,
								BindingResult result ,
								Model model ) {
		
		//エラーなし
		if(!result.hasErrors()) {
			Ally ally = allyForm.toEntity();
			allyRepository.saveAndFlush( ally );
			
			return "redirect:/edit/ally";
			
		//エラーあり	
		}else{
			
			return "edit";
		}
	}
	
	
	//味方側のキャラクター検索に対応
	@PostMapping( "/ally/query" )
	public ModelAndView allyQuery( @ModelAttribute AllyQuery allyQuery , 
									@PageableDefault( page = 0 , size = 5 , sort = "id" ) Pageable pageable ,
									ModelAndView mv ) {
		
		mv.setViewName( "edit" );
		
		Page<Ally> pageList = allyDaoImp.findByCriteria( allyQuery , pageable );
		
		session.setAttribute( "allyQuery" , allyQuery );
		session.setAttribute( "allyList" , pageList.getContent() );
		session.setAttribute( "prevPageable" , pageable );
		session.setAttribute( "mode" , "ally" );
		
		mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "todoPage", pageList );
		
		return mv;
	}
	
	
	//味方側のキャラクター削除に対応
	@PostMapping( "/ally/delete/{id}" )
	public String allyDelete( @PathVariable( name = "id" ) int id ,
							  Model model ) {
		
		allyRepository.deleteById( id );
		
		return "redirect:/edit/ally";
	}
	
	
	
	
	
	
	
	
	//-------------------------------------------------------------
	//------------コントローラークラスを別に分ける予定-------------
	//-------------------------------------------------------------
	
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
