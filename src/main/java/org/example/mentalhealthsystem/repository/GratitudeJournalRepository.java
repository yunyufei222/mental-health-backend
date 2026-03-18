package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.GratitudeJournal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface GratitudeJournalRepository extends JpaRepository<GratitudeJournal, Long> {
    Optional<GratitudeJournal> findByUserIdAndDate(Long userId, LocalDate date);
    Page<GratitudeJournal> findByUserIdOrderByDateDesc(Long userId, Pageable pageable);
}