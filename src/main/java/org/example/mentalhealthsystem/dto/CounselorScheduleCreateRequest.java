package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CounselorScheduleCreateRequest {
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}