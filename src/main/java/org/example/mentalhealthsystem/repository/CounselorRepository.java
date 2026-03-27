package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Counselor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CounselorRepository extends JpaRepository<Counselor, Long> {
    Page<Counselor> findByStatus(Integer status, Pageable pageable);
}