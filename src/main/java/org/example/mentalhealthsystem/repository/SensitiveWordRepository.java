package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.SensitiveWord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SensitiveWordRepository extends JpaRepository<SensitiveWord, Long> {
    List<SensitiveWord> findAll(); // 用于加载敏感词列表
}