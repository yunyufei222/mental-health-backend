package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.ArticleReadRecordDTO;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.UserArticleReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/reads")
@CrossOrigin(origins = "http://localhost:5174")
public class UserReadController {

    @Autowired
    private UserArticleReadService readService;

    @GetMapping
    public ResponseEntity<Page<ArticleReadRecordDTO>> getMyReadHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by("readAt").descending());
        return ResponseEntity.ok(readService.getUserReadHistory(currentUser.getId(), pageable));
    }
}