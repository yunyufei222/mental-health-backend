package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.CounselorDTO;
import org.example.mentalhealthsystem.dto.ScheduleDTO;
import org.example.mentalhealthsystem.repository.CounselorRepository;
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
import org.example.mentalhealthsystem.dto.CounselorScheduleCreateRequest;
import org.example.mentalhealthsystem.dto.CounselorScheduleDTO;
import org.example.mentalhealthsystem.dto.ScheduleDTO;
import org.example.mentalhealthsystem.entity.Counselor;
import org.example.mentalhealthsystem.entity.CounselorSchedule;
import org.example.mentalhealthsystem.entity.Role;
import org.example.mentalhealthsystem.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/counselors")
@CrossOrigin(origins = "http://localhost:5174")
public class CounselorController {
    @Autowired
    private CounselorRepository counselorRepository;
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
    // 新增：咨询师添加自己的排班
    @PostMapping("/schedules")
    public ResponseEntity<CounselorScheduleDTO> addSchedule(
            @RequestBody CounselorScheduleCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        if (currentUser.getRole() != Role.COUNSELOR) {
            return ResponseEntity.status(403).build();
        }
        Counselor counselor = counselorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("咨询师资料不存在"));
        CounselorSchedule schedule = counselorService.addSchedule(counselor.getId(), request);
        CounselorScheduleDTO dto = new CounselorScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setIsBooked(schedule.getIsBooked());
        return ResponseEntity.ok(dto);
    }
    // 咨询师获取自己的排班列表
    @GetMapping("/my/schedules")
    public ResponseEntity<List<CounselorScheduleDTO>> getMySchedules(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null || currentUser.getRole() != Role.COUNSELOR) {
            return ResponseEntity.status(401).build();
        }
        Counselor counselor = counselorRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("咨询师资料不存在"));
        List<CounselorScheduleDTO> schedules = counselorService.getCounselorSchedules(counselor.getId());
        return ResponseEntity.ok(schedules);
    }
}