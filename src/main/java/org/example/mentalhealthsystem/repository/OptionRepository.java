package org.example.mentalhealthsystem.repository;

import org.example.mentalhealthsystem.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findByQuestionIdOrderBySortOrder(Long questionId);
}