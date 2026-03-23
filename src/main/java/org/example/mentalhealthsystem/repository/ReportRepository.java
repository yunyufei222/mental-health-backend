package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Report;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
    Page<Report> findByStatus(Integer status, Pageable pageable);
}