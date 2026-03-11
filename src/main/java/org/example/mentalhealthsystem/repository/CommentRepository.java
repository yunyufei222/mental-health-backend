package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 分页获取文章的顶级评论（parent_id IS NULL）
    Page<Comment> findByArticleIdAndParentIsNullAndStatusOrderByCreatedAtDesc(
            Long articleId, Integer status, Pageable pageable);

    // 获取指定顶级评论的所有直接回复（按时间升序）
    List<Comment> findByParentIdInAndStatusOrderByCreatedAtAsc(List<Long> parentIds, Integer status);

    // 获取文章的所有评论（用于统计等）
    List<Comment> findByArticleIdAndStatus(Long articleId, Integer status);
}