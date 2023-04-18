package com.example.rpgdata.dao;

import java.util.List;

import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.query.MagicQuery;

public interface MagicDao {
	List<Magic> findByCriteria( MagicQuery magicQuery );
}
