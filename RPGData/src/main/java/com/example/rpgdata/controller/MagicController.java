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

import com.example.rpgdata.dao.MagicDaoImp;
import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.form.MagicForm;
import com.example.rpgdata.query.MagicQuery;
import com.example.rpgdata.repository.AllyRepository;
import com.example.rpgdata.repository.MagicRepository;
import com.example.rpgdata.support.MagicList;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MagicController {

	private final MagicRepository magicRepository;
	private final AllyRepository  allyRepository;
	private final HttpSession	  session;
	
	@PersistenceContext
	private EntityManager entityManager;
	private MagicDaoImp magicDaoImp;
	
	//タイミングをズラして初期化
	@PostConstruct
	public void init() {
		magicDaoImp = new MagicDaoImp( entityManager );
	}
	
	
    //プレイアブルキャラクターの魔法一覧に対応
    @GetMapping("/edit/magic")
    public ModelAndView magicEdit( ModelAndView mv ) {
    	
        mv.setViewName( "magic" );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( magicRepository );
		
		session.setAttribute( "announcement" , "normal" );
        session.setAttribute( "magicmode" , "edit" );
        session.setAttribute( "magicQuery" , new MagicQuery() );
        mv.addObject( "magicAllList" , magicAllList );
        
		return mv;
		
    }
    
    
	//魔法の名前押下に対応→更新画面へ遷移
    @GetMapping("/magic/{id}")
    public ModelAndView allyById( @PathVariable( name = "id" ) int id , 
    								ModelAndView mv ) {
    	
        mv.setViewName( "magiccreate" );
        Magic magic = magicRepository.findById( id ).orElseThrow();
        
        mv.addObject( "magicForm" , magic );
        session.setAttribute( "mode" , "magicupdate" );
        
        return mv;
    }
    
    
    
	//魔法の新規登録のボタンに対応
	@GetMapping( "/magic/create" )
	public ModelAndView magicCreate( ModelAndView mv ) {
		
		mv.setViewName( "magiccreate" );
		mv.addObject( "magicForm" , new MagicForm() );
		session.setAttribute( "mode" , "magiccreate" );
		
		return mv;
		
	}
	
	
	//魔法の新規登録
	@PostMapping( "/magic/create" )
	public String magicCreateDo( @ModelAttribute @Validated MagicForm magicForm ,
									BindingResult result ,
									Model model ) {
		
		//エラーなし
		if( !result.hasErrors() ) {
			Magic magic = magicForm.toEntity();
			magicRepository.saveAndFlush( magic );
			session.setAttribute( "announcement" , "success" );
			
			return "redirect:/magic/create";
			
		//エラーあり	
		}else{
			return "magiccreate";
		}
		
	}
    
    
    //魔法の更新に対応
    @PostMapping("/magic/update")
    public String updateMagic( @ModelAttribute @Validated MagicForm magicForm ,
								BindingResult result ,
								Model model ) {
		//エラーなし
		if( !result.hasErrors() ) {
			Magic magic = magicForm.toEntity();
			magicRepository.saveAndFlush( magic );
			session.setAttribute( "announcement" , "success" );
			
			return "redirect:/magic/" + magicForm.getId() ;
			
		//エラーあり	
		}else{
			return "magiccreate";
		}
    	
    }
	
	
    //プレイアブルキャラクターを選択→使用可能な魔法一覧に対応
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
        session.setAttribute( "magicQuery" , new MagicQuery() );
        mv.addObject( "magicList" , magicList );
        mv.addObject( "magicAllList" , magicAddPossibleList );
        
		return mv;
		
    }
    
    
    //プレイアブルキャラクターの新規作成→魔法登録に対応
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
    
    
    //プレイアブルキャラクターの使用可能な魔法を追加
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
	
	
	//魔法検索に対応
	@PostMapping( "/magic/query" )
	public ModelAndView allyQuery( @ModelAttribute MagicQuery magicQuery , 
									ModelAndView mv ) {
		
		mv.setViewName( "magic" );
		
		//検索条件に合致する魔法リストを生成
        List<Magic> magicList = magicDaoImp.findByCriteria( magicQuery );
        
        //魔法リストから例外対策のダミー魔法を除外
        magicList = MagicList.create( magicList );
        
		session.setAttribute( "magicQuery" , magicQuery );
		mv.addObject( "magicList" , magicList );
		mv.addObject( "magicAllList" , magicList );
		
		return mv;
	}
	
	
	//魔法そのものの削除に対応
	@PostMapping( "/magic/delete/{id}" )
	public String magicDelete( @PathVariable( name = "id" ) int id ,
								Model model ) {
		
		magicRepository.deleteById( id );
		
		return "redirect:/edit/magic";
	}
}
