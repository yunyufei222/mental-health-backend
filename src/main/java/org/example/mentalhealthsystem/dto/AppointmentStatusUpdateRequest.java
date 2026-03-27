package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class AppointmentStatusUpdateRequest {
    private String status; // PENDING, CONFIRMED, COMPLETED, CANCELLED
}