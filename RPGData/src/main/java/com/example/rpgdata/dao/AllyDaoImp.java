package com.example.rpgdata.dao;

import java.util.ArrayList;
import java.util.List;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.entity.Ally_;
import com.example.rpgdata.query.AllyQuery;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AllyDaoImp implements AllyDao{
	
	private final EntityManager entityManager;
	
	@Override
	public List<Ally> findByCriteria( AllyQuery allyQuery ) {
		
		CriteriaBuilder 	builder    = entityManager.getCriteriaBuilder();
		CriteriaQuery<Ally> query  	   = builder.createQuery( Ally.class );
		Root<Ally>          root 	   = query.from( Ally.class );
		List<Predicate>     predicates = new ArrayList<>();
	
		String name = "%" + allyQuery.getName() + "%";
		predicates.add(builder.like(root.get( Ally_.NAME ) , name ));
		
		//SELECT作成
		Predicate[] predArray = new Predicate[ predicates.size() ];
		predicates.toArray( predArray );
		query = query.select( root )
						.where(predArray)
						.orderBy(builder.asc(root.get( Ally_.id )));
		
		//検索
		List<Ally> list = entityManager
							.createQuery( query )
							.getResultList();
		
		return list;
	}

}
