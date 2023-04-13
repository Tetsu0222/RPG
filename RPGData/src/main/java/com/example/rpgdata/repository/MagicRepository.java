package com.example.rpgdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.Magic;

@Repository
public interface MagicRepository extends JpaRepository<Magic, Integer>{

}
