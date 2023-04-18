package com.example.rpgdata.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.entity.Magic_;
import com.example.rpgdata.query.MagicQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;


@AllArgsConstructor
public class MagicDaoImp implements MagicDao{
	
	private final EntityManager entityManager;
	
	@Override
	public List<Magic> findByCriteria( MagicQuery magicQuery ) {
		
		//おまじない
		CriteriaBuilder 	builder    = entityManager.getCriteriaBuilder();
		CriteriaQuery<Magic> query  	   = builder.createQuery( Magic.class );
		Root<Magic>          root 	   = query.from( Magic.class );
		List<Predicate>     predicates = new ArrayList<>();
		
		//魔法名に対応
		String name = magicQuery.getName().length() > 0 ? "%" + magicQuery.getName() + "%" : "%" ;

		//検索条件を格納するためのリストへ追加
		predicates.add( builder.like( root.get( Magic_.NAME ) , name ));
		
		//カテゴリーに対応
		if( magicQuery.getCategory().length() > 0 ) {
			String category = magicQuery.getCategory();
			predicates.add( builder.like( root.get( Magic_.CATEGORY ) , category ));
		}
		
		//効果範囲に対応
		if( magicQuery.getRange().length() > 0 ) {
			String range = magicQuery.getRange();
			predicates.add( builder.like( root.get( Magic_.RANGE ) , range ));
		}
		
		//バフ・デバフに対応
		if( magicQuery.getBuffcategory().length() > 0 ) {
			String buffCategory = magicQuery.getBuffcategory();
			predicates.add( builder.like( root.get( Magic_.BUFFCATEGORY ) , buffCategory ));
		}
		
		Predicate[] predArray = new Predicate[ predicates.size() ];
		predicates.toArray( predArray );
		query = query.select( root ).where( predArray ).orderBy( builder.asc( root.get( Magic_.id )));
		List<Magic> list = entityManager.createQuery( query ).getResultList();
		
		return list;
	}

}
