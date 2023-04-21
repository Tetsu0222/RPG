package com.example.rpgdata.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.rpgdata.entity.AttachedFile;

@Repository
public interface AttachedFileRepository extends JpaRepository<AttachedFile, Integer> {
    // todoIdをキーに検索する(idの昇順)
    List<AttachedFile> findByEnemyIdOrderById( Integer enemyId );
}
