package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 分页查询已发布的帖子（可按类型筛选）
    @Query("SELECT p FROM Post p WHERE p.status = 1 " +
            "AND (:type IS NULL OR p.type = :type) " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findPublishedPosts(@Param("type") Post.PostType type, Pageable pageable);

    // 管理员查询所有帖子（包括待审核）
    Page<Post> findByStatus(Integer status, Pageable pageable);

    // 增加帖子阅读数（原子操作）
    @Modifying
    @Query("UPDATE Post p SET p.viewCount = p.viewCount + 1 WHERE p.id = :id")
    void incrementViewCount(@Param("id") Long id);
}