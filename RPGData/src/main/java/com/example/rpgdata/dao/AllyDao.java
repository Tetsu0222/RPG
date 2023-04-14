package com.example.rpgdata.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.query.AllyQuery;

public interface AllyDao {
	Page<Ally> findByCriteria( AllyQuery allyQuery , Pageable pageable );
}
