package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Post;
import org.example.mentalhealthsystem.entity.PostStrength;
import org.example.mentalhealthsystem.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostStrengthRepository extends JpaRepository<PostStrength, Long> {
    Optional<PostStrength> findByPostAndUserAndStrength(Post post, User user, String strength);
    boolean existsByPostAndUserAndStrength(Post post, User user, String strength);

    @Modifying
    @Query("DELETE FROM PostStrength ps WHERE ps.post.id = :postId AND ps.user.id = :userId AND ps.strength = :strength")
    void deleteByPostIdAndUserIdAndStrength(@Param("postId") Long postId, @Param("userId") Long userId, @Param("strength") String strength);
}