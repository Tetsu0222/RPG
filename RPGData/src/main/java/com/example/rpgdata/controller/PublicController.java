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
import org.springframework.web.bind.annotation.RequestParam;
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
import com.example.rpgdata.support.MagicList;

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
    
    
    //魔法一覧に対応
    @GetMapping("/ally/magic/{id}")
    public ModelAndView magic( @PathVariable( name = "id" ) int id , 
    							ModelAndView mv ) {
    	
        mv.setViewName( "magic" );
        
        //選択されたキャラクターの情報を取得
        Ally ally = allyRepository.findById( id ).orElseThrow();
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Magic> magicList = MagicList.create( ally , magicRepository );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( magicRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
		List<Magic> magicAddPossibleList = MagicList.create( magicList , magicAllList );
		
        session.setAttribute( "magicmode" , "reading" );
        session.setAttribute( "ally" , ally );
        mv.addObject( "magicList" , magicList );
        mv.addObject( "magicAllList" , magicAddPossibleList );
        
		return mv;
		
    }
    
    
    //新規作成→魔法登録に対応
    @PostMapping("/ally/magic")
    public ModelAndView magic2( ModelAndView mv ) {
    	
        mv.setViewName( "magic" );
        
    	//セッションからプレイアブルキャラクターの情報を取得
    	Ally ally = (Ally)session.getAttribute( "ally" );
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Magic> magicList = MagicList.create( ally , magicRepository );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( magicRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
		List<Magic> magicAddPossibleList = MagicList.create( magicList , magicAllList );
		
        session.setAttribute( "magicmode" , "reading" );
        session.setAttribute( "ally" , ally );
        mv.addObject( "magicList" , magicList );
        mv.addObject( "magicAllList" , magicAddPossibleList );
        
		return mv;
		
    }
    
    
    //キャラクターの使用可能な魔法を追加
    @PostMapping("/ally/magic/add")
    public String magicAdd( @RequestParam( name = "magicAddId" ) String magicAddId ,
    						Model model ) {
    	
    	//セッションからプレイアブルキャラクターの情報を取得
    	Ally ally = (Ally)session.getAttribute( "ally" );
    	
    	//魔法の追加処理
    	String magic = MagicList.add( ally , magicAddId );
		
    	//追加された魔法を設定
		ally.setMagic( magic );
		
		//保存
		allyRepository.saveAndFlush( ally );
		
    	return "redirect:/ally/magic/" + ally.getId();
    }
    
    
    
	//キャラクターの使用可能な魔法を削除
	@PostMapping( "/ally/magic/delete/{id}" )
	public String allyMagicDelete( @PathVariable( name = "id" ) String magicId ,
							  		Model model ) {
		
		//セッションからプレイアブルキャラクターの情報を取得
		Ally ally = (Ally)session.getAttribute( "ally" );
		
		//魔法の削除を実行
		String magic = MagicList.delete( ally , magicId );
		
		//削除後の魔法を設定
		ally.setMagic( magic );
		
		//保存
		allyRepository.saveAndFlush( ally );
		
		return "redirect:/ally/magic/" + ally.getId();
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
