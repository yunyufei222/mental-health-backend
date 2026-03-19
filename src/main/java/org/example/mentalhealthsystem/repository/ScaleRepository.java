package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Scale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScaleRepository extends JpaRepository<Scale, Long> {
    Optional<Scale> findByCode(String code);
    Page<Scale> findByIsActiveTrue(Pageable pageable);
}