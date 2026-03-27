package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Counselor;
import org.example.mentalhealthsystem.entity.CounselorSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CounselorScheduleRepository extends JpaRepository<CounselorSchedule, Long> {
    List<CounselorSchedule> findByCounselorAndDateAfterOrderByDateAscStartTimeAsc(Counselor counselor, LocalDate date);
    List<CounselorSchedule> findByCounselorIdAndDateAndIsBookedFalse(Long counselorId, LocalDate date);
}