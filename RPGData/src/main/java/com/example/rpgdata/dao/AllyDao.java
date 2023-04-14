package com.example.rpgdata.dao;

import java.util.List;

import com.example.rpgdata.entity.Ally;
import com.example.rpgdata.query.AllyQuery;

public interface AllyDao {
	List<Ally> findByCriteria( AllyQuery allyQuery );
}
