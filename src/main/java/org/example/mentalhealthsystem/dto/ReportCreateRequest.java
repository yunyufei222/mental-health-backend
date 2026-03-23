package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class ReportCreateRequest {
    private String targetType; // POST, COMMENT
    private Long targetId;
    private String reason;
}