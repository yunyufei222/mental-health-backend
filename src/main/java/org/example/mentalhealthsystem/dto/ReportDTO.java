package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReportDTO {
    private Long id;
    private Long reporterId;
    private String reporterName;
    private String targetType;
    private Long targetId;
    private String reason;
    private Integer status;
    private LocalDateTime createdAt;
}