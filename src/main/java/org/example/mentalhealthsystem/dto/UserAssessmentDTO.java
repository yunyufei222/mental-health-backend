package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class UserAssessmentDTO {
    private Long id;
    private Long scaleId;
    private String scaleName;
    private Integer totalScore;
    private Map<String, Integer> dimensionScores; // 维度得分
    private String interpretation;                // 结果解读
    private LocalDateTime createdAt;
}