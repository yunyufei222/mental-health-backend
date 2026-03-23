package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.PostComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {
    Page<PostComment> findByPostIdAndParentIsNullOrderByCreatedAtDesc(Long postId, Pageable pageable);
    Page<PostComment> findByPostIdAndParentIdOrderByCreatedAtAsc(Long postId, Long parentId, Pageable pageable);
}