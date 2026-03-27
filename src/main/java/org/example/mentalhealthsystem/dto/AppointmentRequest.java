package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AppointmentRequest {
    private Long scheduleId;
    private String problemDesc;
    private String consultationType; // ONLINE / OFFLINE
}