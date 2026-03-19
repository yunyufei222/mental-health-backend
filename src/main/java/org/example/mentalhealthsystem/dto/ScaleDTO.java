package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ScaleDTO {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String instruction;
    private Integer dimensionCount;
    private Integer questionCount;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuestionDTO> questions; // 可选，用于详情
}