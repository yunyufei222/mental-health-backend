package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.CounselorDTO;
import org.example.mentalhealthsystem.dto.ScheduleDTO;
import org.example.mentalhealthsystem.service.CounselorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/counselors")
@CrossOrigin(origins = "http://localhost:5174")
public class CounselorController {

    @Autowired
    private CounselorService counselorService;

    @GetMapping
    public ResponseEntity<Page<CounselorDTO>> getCounselors(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(counselorService.getActiveCounselors(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CounselorDTO> getCounselorDetail(@PathVariable Long id) {
        return ResponseEntity.ok(counselorService.getCounselorDetail(id));
    }

    @GetMapping("/{id}/schedules")
    public ResponseEntity<List<ScheduleDTO>> getAvailableSchedules(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(counselorService.getAvailableSchedules(id, date));
    }
}