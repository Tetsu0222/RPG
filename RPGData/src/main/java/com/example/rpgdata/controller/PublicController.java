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
import com.example.rpgdata.form.AllyForm;
import com.example.rpgdata.query.AllyQuery;
import com.example.rpgdata.repository.AllyRepository;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class PublicController {
	
	private final AllyRepository  			allyRepository;
	private final HttpSession				session;
	
	@PersistenceContext
	private EntityManager entityManager;
	private AllyDaoImp allyDaoImp;
	
	//タイミングをズラして初期化
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
		
		mv.setViewName( "allyedit" );
		
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

		mv.addObject( "allyList" , pageList.getContent());
		mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "allyPage", pageList );
		
		return mv;
	}
	
	
	//ページリンクの押下に対応
    @GetMapping("/ally/query")
    public ModelAndView queryTodo( @PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
                                   ModelAndView mv) {
    	
    	mv.setViewName( "allyedit" );
    	
        //現在のページ位置を保存
        session.setAttribute( "prevPageable" , pageable );
        
        //sessionに保存されている条件で検索
        AllyQuery allyQuery = (AllyQuery)session.getAttribute( "allyQuery" );
        Page<Ally> pageList = allyDaoImp.findByCriteria( allyQuery , pageable );
        
        session.setAttribute( "allyQuery" , allyQuery );
        
		mv.addObject( "allyList" , pageList.getContent());
		mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "allyPage", pageList );

        return mv;
    }
	
	
	//味方側のキャラクター新規登録のボタンに対応
	@GetMapping( "/ally/create" )
	public ModelAndView allyCreate( ModelAndView mv ) {
		
		List<Ally> allyList = allyRepository.findAll();
		
		mv.setViewName( "allycreate" );
		mv.addObject( "allyForm" , new AllyForm() );
		
		session.setAttribute( "allyList" , allyList );
		session.setAttribute( "mode" , "create" );
		
		return mv;
		
	}
    
    
	//味方側のキャラクター新規登録に対応
	@PostMapping( "/ally/create" )
	public String allyCreate( @ModelAttribute @Validated AllyForm allyForm ,
								BindingResult result ,
								Model model ) {
		
		//エラーなし
		if( !result.hasErrors() ) {
			Ally ally = allyForm.toEntity();
			ally = allyRepository.saveAndFlush( ally );
			session.setAttribute( "ally" , ally );
			session.setAttribute( "announcement" , "magicskill" );
			
			return "redirect:/ally/create";
			
		//エラーあり	
		}else{
			return "allycreate";
		}
	}
	
	
	//プレイアブルキャラクターの名前押下に対応→更新画面へ遷移
    @GetMapping("/ally/{id}")
    public ModelAndView allyById( @PathVariable( name = "id" ) int id , 
    								ModelAndView mv ) {
    	
        mv.setViewName( "allycreate" );
        Ally ally = allyRepository.findById( id ).orElseThrow();
        
        mv.addObject( "allyForm" , ally );
        session.setAttribute( "mode" , "update" );
        
        return mv;
    }
    
    
	//魔法や特技の一覧画面からプレイアブルキャラクターの詳細画面へ推移（戻るボタン対応）
    @PostMapping("/ally/{id}")
    public ModelAndView allyById2( @PathVariable( name = "id" ) int id , 
    								ModelAndView mv ) {
    	
        mv.setViewName( "allycreate" );
        Ally ally = allyRepository.findById( id ).orElseThrow();
        
        mv.addObject( "allyForm" , ally );
        session.setAttribute( "mode" , "update" );
        
        return mv;
    }
    
    
    //更新に対応
    @PostMapping("/ally/update")
    public String update( @ModelAttribute @Validated AllyForm allyForm ,
    						BindingResult result ,
    						Model model ) {
    	
		//エラーなし
		if( !result.hasErrors() ) {
			Ally ally = allyForm.toEntity();
			allyRepository.saveAndFlush( ally );
			session.setAttribute( "announcement" , "success" );
			
			return "redirect:/ally/" + allyForm.getId() ;
			
		//エラーあり	
		}else{
			return "allycreate";
		}
    }
	
	
	//味方側のキャラクター検索に対応
	@PostMapping( "/ally/query" )
	public ModelAndView allyQuery( @ModelAttribute AllyQuery allyQuery , 
									@PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
									ModelAndView mv ) {
		
		mv.setViewName( "allyedit" );
		
        Page<Ally> pageList = allyDaoImp.findByCriteria( allyQuery , pageable );
        
		session.setAttribute( "allyQuery" , allyQuery );
		session.setAttribute( "prevPageable" , pageable );
		
		mv.addObject( "allyList" , pageList.getContent());
		mv.addObject( "allyForm" , new AllyForm() );
		mv.addObject( "allyPage", pageList );
		
		return mv;
	}
	
	
	//味方側のキャラクター削除に対応
	@PostMapping( "/ally/delete/{id}" )
	public String allyDelete( @PathVariable( name = "id" ) int id ,
							  Model model ) {
		
		allyRepository.deleteById( id );
		
		return "redirect:/edit/ally";
	}
	
	
	//戻るに対応
	@PostMapping( "/ally/cancel" )
	public String cancel( Model model ) {
		
		session.setAttribute( "announcement" , "normal" );
		session.setAttribute( "mode" , "normal" );
		
		return "redirect:/edit/ally";
	}
	
	
	//新規作成→登録完了に対応
	@PostMapping( "/ally/complete" )
	public String complete( Model model ) {
		
		session.invalidate();
		
		return "redirect:/edit/ally";
	}
	
}
