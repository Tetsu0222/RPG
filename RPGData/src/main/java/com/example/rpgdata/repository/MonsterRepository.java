package com.example.rpgdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.Monster;

@Repository
public interface MonsterRepository extends JpaRepository<Monster, Integer>{

}
