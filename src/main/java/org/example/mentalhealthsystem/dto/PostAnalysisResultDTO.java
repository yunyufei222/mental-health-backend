package org.example.mentalhealthsystem.dto;

import lombok.Data;
import java.util.List;

@Data
public class PostAnalysisResultDTO {
    private Long id;
    private Long postId;
    private String emotionPrimary;
    private List<String> emotionSecondary;
    private Integer emotionIntensity;
    private List<String> positiveIndicators;
    private List<String> riskIndicators;
    private Integer wellbeingScore;
    private List<String> suggestions;
    private List<String> recommendedTools;
    private String createdAt;
}