package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AssessmentResultDTO {
    private Long id;
    private Long scaleId;
    private String scaleName;
    private Integer totalScore;
    private Map<String, Integer> dimensionScores;
    private String interpretation;
    private String detailedInterpretation;
    private String createdAt;
}