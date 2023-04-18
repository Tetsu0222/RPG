package com.example.rpgdata.dao;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Ally_;
import com.example.rpgdata.query.AllyQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AllyDaoImp implements AllyDao{
	
	private final EntityManager entityManager;
	
	@Override
	public Page<Ally> findByCriteria( AllyQuery allyQuery , Pageable pageable ) {
		
		//おまじない
		CriteriaBuilder 	builder    = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ally> query  	   = builder.createQuery( Ally.class );
		Root<Ally>          root 	   = query.from( Ally.class );
		List<Predicate>     predicates = new ArrayList<>();
		
		//入力された名前が含まれるものを検索対象とする。
		String name = "%" + allyQuery.getName() + "%";
		
		//今後の検索条件の追加に備えて、リストにて検索条件を格納
		predicates.add( builder.like( root.get( Ally_.NAME ) , name ));
		
		//検索条件を格納する配列を生成
		Predicate[] predArray = new Predicate[ predicates.size() ];
		
		//リストから配列へ変換
		predicates.toArray( predArray );
		
		//配列を元にクエリ文を生成
		query = query.select( root )
						.where(predArray)
						.orderBy(builder.asc(root.get( Ally_.id )));
		
		
	    TypedQuery<Ally> typedQuery = entityManager.createQuery( query );
	    
	    //総レコード数の取得
	    int totalRows = typedQuery.getResultList().size();
	    
	    //先頭レコードの位置設定
	    typedQuery.setFirstResult( pageable.getPageNumber() * pageable.getPageSize() );
	    
	    //1ページあたりの件数
	    typedQuery.setMaxResults( pageable.getPageSize() );
		
	    Page<Ally> page = new PageImpl<Ally>( typedQuery.getResultList() , pageable , totalRows );
		
		return page;
	}

}
