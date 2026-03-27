package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
public class AppointmentDTO {
    private Long id;
    private Long counselorId;
    private String counselorName;
    private String counselorAvatar;
    private LocalDate appointmentDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String problemDesc;
    private String consultationType;
    private String status;
    private String feedback;
    private Integer rating;
    private LocalDateTime createdAt;
}