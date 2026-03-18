package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ArticleRepository extends JpaRepository<Article, Long> {

    // 分页查询已发布的文章（支持分类、标签、关键词筛选）
    @Query("SELECT DISTINCT a FROM Article a LEFT JOIN a.tags t WHERE a.status = 1 " +
            "AND (:categoryId IS NULL OR a.category.id = :categoryId) " +
            "AND (:tagId IS NULL OR t.id = :tagId) " +
            "AND (:keyword IS NULL OR a.title LIKE %:keyword% OR a.summary LIKE %:keyword%) " +
            "ORDER BY a.isTop DESC, a.publishedAt DESC")
    Page<Article> findPublishedArticles(@Param("categoryId") Long categoryId,
                                        @Param("tagId") Long tagId,
                                        @Param("keyword") String keyword,
                                        Pageable pageable);

    // 增加阅读数
    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    void incrementViewCount(@Param("id") Long id);
}