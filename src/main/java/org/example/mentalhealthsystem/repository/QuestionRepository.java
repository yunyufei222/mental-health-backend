package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByScaleIdOrderBySortOrder(Long scaleId);
}