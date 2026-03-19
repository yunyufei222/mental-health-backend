package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.UserAssessment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserAssessmentRepository extends JpaRepository<UserAssessment, Long> {
    Page<UserAssessment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
}