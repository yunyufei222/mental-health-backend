package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.PostAnalysis;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostAnalysisRepository extends JpaRepository<PostAnalysis, Long> {
    Optional<PostAnalysis> findByPostId(Long postId);
    Page<PostAnalysis> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}