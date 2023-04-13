package com.example.rpgdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.MonsterPattern;

@Repository
public interface MonsterPatternRepository extends JpaRepository<MonsterPattern, Integer>{

}
