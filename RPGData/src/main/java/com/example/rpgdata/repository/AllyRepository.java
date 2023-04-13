package com.example.rpgdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.Ally;

@Repository
public interface AllyRepository extends JpaRepository<Ally, Integer>{

}
