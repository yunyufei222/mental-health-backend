package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Article;
import org.example.mentalhealthsystem.entity.ArticleFavorite;
import org.example.mentalhealthsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleFavoriteRepository extends JpaRepository<ArticleFavorite, Long> {
    Optional<ArticleFavorite> findByArticleAndUser(Article article, User user);

    boolean existsByArticleAndUser(Article article, User user);

    @Modifying
    @Query("DELETE FROM ArticleFavorite af WHERE af.article.id = :articleId AND af.user.id = :userId")
    void deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);
}