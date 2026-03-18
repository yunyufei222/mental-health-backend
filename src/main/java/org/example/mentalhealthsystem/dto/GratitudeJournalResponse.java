package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class GratitudeJournalResponse {
    private Long id;
    private LocalDate date;
    private String item1;
    private String item2;
    private String item3;
    private String reflection;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}