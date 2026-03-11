package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Article;
import org.example.mentalhealthsystem.entity.ArticleLike;
import org.example.mentalhealthsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleAndUser(Article article, User user);

    boolean existsByArticleAndUser(Article article, User user);

    @Modifying
    @Query("DELETE FROM ArticleLike al WHERE al.article.id = :articleId AND al.user.id = :userId")
    void deleteByArticleIdAndUserId(@Param("articleId") Long articleId, @Param("userId") Long userId);
}