package com.example.rpgdata.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.rpgdata.entity.Magic;
import com.example.rpgdata.query.MagicQuery;

public interface MagicDao {
	Page<Magic> findByCriteria( MagicQuery magicQuery ,  Pageable pageable);
}
