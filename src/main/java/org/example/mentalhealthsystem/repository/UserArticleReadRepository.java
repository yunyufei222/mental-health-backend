package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.UserArticleRead;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserArticleReadRepository extends JpaRepository<UserArticleRead, Long> {
    Optional<UserArticleRead> findByUserIdAndArticleId(Long userId, Long articleId);
    Page<UserArticleRead> findByUserIdOrderByReadAtDesc(Long userId, Pageable pageable);
}