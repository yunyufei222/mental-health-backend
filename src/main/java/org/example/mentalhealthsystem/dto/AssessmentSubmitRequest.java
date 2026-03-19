package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssessmentSubmitRequest {
    private Long scaleId;
    private List<Integer> answers; // 选项的score列表或选项ID（根据设计，这里用score值）
}