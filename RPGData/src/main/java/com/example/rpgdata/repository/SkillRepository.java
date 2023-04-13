package com.example.rpgdata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Integer>{

}
