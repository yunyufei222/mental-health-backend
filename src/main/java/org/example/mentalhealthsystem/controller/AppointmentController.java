package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.AppointmentDTO;
import org.example.mentalhealthsystem.dto.AppointmentRequest;
import org.example.mentalhealthsystem.entity.Appointment;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.example.mentalhealthsystem.entity.Role;
import org.example.mentalhealthsystem.entity.Counselor;
import org.example.mentalhealthsystem.repository.CounselorRepository;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "http://localhost:5174")
public class AppointmentController {
    @Autowired
    private CounselorRepository counselorRepository;
    @Autowired
    private AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody AppointmentRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Appointment appointment = appointmentService.createAppointment(currentUser.getId(), request);
        return ResponseEntity.ok(appointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelAppointment(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        appointmentService.cancelAppointment(id, currentUser.getId(), false);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<Page<AppointmentDTO>> getMyAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(appointmentService.getUserAppointments(currentUser.getId(), pageable));
    }
    @GetMapping("/counselor/my")
    public ResponseEntity<Page<AppointmentDTO>> getMyCounselorAppointments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        if (currentUser.getRole() != Role.COUNSELOR) {
            return ResponseEntity.status(403).build();
        }
        Counselor counselor = counselorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("咨询师资料不存在"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<AppointmentDTO> appointments = appointmentService.getCounselorAppointmentsByCounselorId(counselor.getId(), pageable);
        return ResponseEntity.ok(appointments);
    }
}