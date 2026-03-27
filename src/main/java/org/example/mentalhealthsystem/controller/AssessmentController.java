package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.AssessmentResultDTO;
import org.example.mentalhealthsystem.dto.AssessmentSubmitRequest;
import org.example.mentalhealthsystem.dto.UserAssessmentDTO;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.AssessmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/assessments")
@CrossOrigin(origins = "http://localhost:5174")
public class AssessmentController {

    @Autowired
    private AssessmentService assessmentService;

    @PostMapping("/submit")
    public ResponseEntity<AssessmentResultDTO> submitAssessment(
            @RequestBody AssessmentSubmitRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        AssessmentResultDTO result = assessmentService.submitAssessment(currentUser.getId(), request);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取当前用户的测评历史（分页）
     */
    @GetMapping("/history")
    public ResponseEntity<Page<UserAssessmentDTO>> getMyHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UserAssessmentDTO> history = assessmentService.getUserAssessmentHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 获取指定测评记录的详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<AssessmentResultDTO> getAssessmentResult(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        AssessmentResultDTO result = assessmentService.getAssessmentResultById(id, currentUser.getId());
        return ResponseEntity.ok(result);
    }
}