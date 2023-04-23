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
	
	
	
	
    //魔法一覧に対応
    @GetMapping("/edit/magic")
    public ModelAndView magicEdit( ModelAndView mv ,
    		@PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ) {
    	
        mv.setViewName( "magic" );
        
        MagicQuery magicQuery = (MagicQuery)session.getAttribute( "magicQuery" );
        
		//セッションに情報がなければ新規生成
        if( magicQuery == null ) {
        	magicQuery = new MagicQuery();
            session.setAttribute( "magicQuery" , magicQuery );
        }
        
        //前回までのページ設定をセッションから取得
        Pageable prevPageable = (Pageable)session.getAttribute( "prevPageable" );
        
        //セッションに情報がなければ新規生成
        if( prevPageable == null ) {
        	prevPageable = pageable;
        	session.setAttribute( "prevPageable" , prevPageable );
        }
        
        Page<Magic> pageList = magicDaoImp.findByCriteria( magicQuery , prevPageable );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( pageList.getContent() );
		
		session.setAttribute( "announcement" , "normal" );
        session.setAttribute( "magicmode" , "edit" );
        
        mv.addObject( "magicAllList" , magicAllList );
        mv.addObject( "magicPage", pageList );
        
		return mv;
		
    }
    
    
	//ページリンクの押下に対応
    @GetMapping("/magic/query")
    public ModelAndView queryMagic( @PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
                                   	ModelAndView mv) {
    	
    	mv.setViewName( "magic" );
    	
        //現在のページ位置を保存
        session.setAttribute( "prevPageable" , pageable );
        
        //sessionに保存されている条件で検索
        MagicQuery magicQuery = (MagicQuery)session.getAttribute( "magicQuery" );
        Page<Magic> pageList = magicDaoImp.findByCriteria( magicQuery , pageable );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( pageList.getContent() );
        
        session.setAttribute( "magicQuery" , magicQuery );
        
		mv.addObject( "magicAllList" , magicAllList );
		mv.addObject( "magicPage", pageList );

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
	
    
    //プレイアブルキャラクターの使用可能な魔法一覧に対応
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
    
	
    /* バグ発生中のため、ページネーション未実装
    //プレイアブルキャラクターを選択→使用可能な魔法一覧に対応
    @GetMapping("/ally/magic/{id}")
    public ModelAndView magic( @PathVariable( name = "id" ) int id , 
    							@PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
    							ModelAndView mv ) {
    	
        mv.setViewName( "magic" );
        
        MagicQuery magicQuery = (MagicQuery)session.getAttribute( "magicQuery" );
        
		//セッションに情報がなければ新規生成
        if( magicQuery == null ) {
        	magicQuery = new MagicQuery();
            session.setAttribute( "magicQuery" , magicQuery );
        }
        
        //前回までのページ設定をセッションから取得
        Pageable prevPageable = (Pageable)session.getAttribute( "prevPageable" );
        
        //セッションに情報がなければ新規生成
        if( prevPageable == null ) {
        	prevPageable = pageable;
        	session.setAttribute( "prevPageable" , prevPageable );
        }
        
        //選択されたキャラクターの情報を取得
        Ally ally = allyRepository.findById( id ).orElseThrow();
		
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Magic> allyMagicList = MagicList.create( ally , magicRepository );
        Page<Magic> pageList = magicDaoImp.findByCriteria( magicQuery , prevPageable );

		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicSourceList = MagicList.create( pageList.getContent() );
        
		//プレイアブルキャラクターの使用可能な魔法だけに限定
		List<Magic> magicList = magicSourceList.stream()
				.filter( magic -> allyMagicList.contains( magic ) )
				.toList();
				
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( magicRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
		List<Magic> magicAddPossibleList = MagicList.create( magicList , magicAllList );
		
        session.setAttribute( "magicmode" , "reading" );
        session.setAttribute( "ally" , ally );
        session.setAttribute( "magicQuery" , magicQuery );
        
        mv.addObject( "magicList" , magicList );
        mv.addObject( "magicAllList" , magicAddPossibleList );
        mv.addObject( "magicPage", pageList );
        
		return mv;
		
    }
    */
    
    //バグ発生のためリンク削除 修正予定
	//プレイアブルキャラクターの使用可能な魔法のページリンクの押下に対応
    @GetMapping("/magic/ally/query")
    public ModelAndView queryAllyMagic( @PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
                                   		ModelAndView mv) {
    	
    	mv.setViewName( "magic" );
    	
        //現在のページ位置を保存
        session.setAttribute( "prevPageable" , pageable );
        
        //sessionに保存されている条件で検索
    	Ally ally = (Ally)session.getAttribute( "ally" );
        MagicQuery magicQuery = (MagicQuery)session.getAttribute( "magicQuery" );
        
        //キャラクターの使用可能な魔法を一覧で格納するリストを生成
        List<Magic> allyMagicList = MagicList.create( ally , magicRepository );
        Page<Magic> pageList = magicDaoImp.findByCriteria( magicQuery , pageable );
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicSourceList = MagicList.create( pageList.getContent() );
        
		//プレイアブルキャラクターの使用可能な魔法だけに限定
		List<Magic> magicList = magicSourceList.stream()
				.filter( magic -> allyMagicList.contains( magic ) )
				.toList();
        
		//ダミー魔法を除いた全魔法リストを生成
		List<Magic> magicAllList = MagicList.create( magicRepository );
		
		//全魔法リストから追加可能な魔法だけを抽出
		List<Magic> magicAddPossibleList = MagicList.create( magicList , magicAllList );
		
		session.setAttribute( "magicmode" , "reading" );
        session.setAttribute( "magicQuery" , magicQuery );
        session.setAttribute( "ally" , ally );
        
        mv.addObject( "magicList" , magicList );
        mv.addObject( "magicAllList" , magicAddPossibleList );
		mv.addObject( "magicPage", pageList );

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
        session.setAttribute( "magicQuery" , new MagicQuery() );
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
	public ModelAndView magicQuery( @ModelAttribute MagicQuery magicQuery , 
									@PageableDefault( page = 0 , size = 10 , sort = "id" ) Pageable pageable ,
									ModelAndView mv ) {
		
		mv.setViewName( "magic" );
		
		//検索条件に合致する魔法リストを生成
		Page<Magic> pageList = magicDaoImp.findByCriteria( magicQuery , pageable );
        
        //魔法リストから例外対策のダミー魔法を除外
		List<Magic> magicAllList = MagicList.create( pageList.getContent() );
        
		session.setAttribute( "magicQuery" , magicQuery );
		session.setAttribute( "prevPageable" , pageable );
		
		mv.addObject( "magicAllList" , magicAllList );
		mv.addObject( "magicPage", pageList );
		
		return mv;
	}
	
	
	//プレイアブルキャラクターの魔法検索に対応
	@PostMapping( "/ally/magic/query" )
	public ModelAndView allyMagicQuery( @ModelAttribute MagicQuery magicQuery , 
										ModelAndView mv ) {
		
		mv.setViewName( "magic" );
		
		//セッションからプレイアブルキャラクターの情報を取得
		Ally ally = (Ally)session.getAttribute( "ally" );
		
		//検索条件に合致する魔法リストを生成
        List<Magic> magicList = magicDaoImp.findByCriteria( magicQuery );
        
        //プレイアブルキャラクターの使用可能な魔法を取得
        List<Magic> allyMagicList = MagicList.create( ally , magicRepository );
        
        //魔法リストから例外対策のダミー魔法を除外
        List<Magic> magicListNoDummy = MagicList.create( magicList );
        
        List<Magic> magicQueryList = allyMagicList.stream()
        		.filter( s -> magicListNoDummy.contains( s ))
        		.toList();
        
		session.setAttribute( "magicQuery" , magicQuery );
		mv.addObject( "magicList" , magicQueryList );
		mv.addObject( "magicAllList" , magicListNoDummy );
		
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
