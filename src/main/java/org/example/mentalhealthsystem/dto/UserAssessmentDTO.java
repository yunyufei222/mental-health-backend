package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserAssessmentDTO {
    private Long id;
    private Long scaleId;
    private String scaleName;
    private Integer totalScore;
    private LocalDateTime createdAt;
}