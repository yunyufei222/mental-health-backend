package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.GratitudeJournalRequest;
import org.example.mentalhealthsystem.dto.GratitudeJournalResponse;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.GratitudeJournalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tools/gratitude")
@CrossOrigin(origins = "http://localhost:5174")
public class GratitudeJournalController {

    @Autowired
    private GratitudeJournalService journalService;

    // 获取今天的日记
    @GetMapping("/today")
    public ResponseEntity<GratitudeJournalResponse> getToday(@AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        GratitudeJournalResponse today = journalService.getTodayJournal(currentUser.getId());
        return ResponseEntity.ok(today);
    }

    // 保存今天的日记（创建或更新）
    @PostMapping("/today")
    public ResponseEntity<GratitudeJournalResponse> saveToday(
            @RequestBody GratitudeJournalRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        GratitudeJournalResponse saved = journalService.saveTodayJournal(currentUser.getId(), request);
        return ResponseEntity.ok(saved);
    }

    // 获取历史日记（分页）
    @GetMapping("/history")
    public ResponseEntity<Page<GratitudeJournalResponse>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("date").descending());
        Page<GratitudeJournalResponse> history = journalService.getHistory(currentUser.getId(), pageable);
        return ResponseEntity.ok(history);
    }
    // GratitudeJournalController.java 中添加
    @GetMapping("/monthly")
    public ResponseEntity<List<GratitudeJournalResponse>> getMonthly(
            @RequestParam int year,
            @RequestParam int month,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        List<GratitudeJournalResponse> journals = journalService.getMonthlyJournal(currentUser.getId(), year, month);
        return ResponseEntity.ok(journals);
    }
}