package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Page<Appointment> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Appointment> findByCounselorIdOrderByCreatedAtDesc(Long counselorId, Pageable pageable);
}