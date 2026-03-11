package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.ArticleCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleCategoryRepository extends JpaRepository<ArticleCategory, Long> {
    boolean existsByName(String name);
}