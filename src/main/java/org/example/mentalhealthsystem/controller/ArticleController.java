package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.ArticleDTO;
import org.example.mentalhealthsystem.dto.ArticleDetailDTO;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.ArticleService;
import org.example.mentalhealthsystem.service.UserArticleReadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "http://localhost:5174")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private UserArticleReadService readService;

    // 获取已发布的文章列表（支持分类、标签、关键词搜索）
    @GetMapping
    public ResponseEntity<Page<ArticleDTO>> getArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("isTop").descending().and(Sort.by("publishedAt").descending()));
        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<ArticleDTO> articles = articleService.getPublishedArticles(categoryId, tagId, keyword, pageable, userId);
        return ResponseEntity.ok(articles);
    }

    // 获取文章详情（并记录阅读历史）
    @GetMapping("/{id}")
    public ResponseEntity<ArticleDetailDTO> getArticleDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        ArticleDetailDTO detail = articleService.getArticleDetail(id, userId);
        if (userId != null) {
            readService.recordRead(userId, id);
        }
        return ResponseEntity.ok(detail);
    }

    // 点赞/取消点赞
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        boolean liked = articleService.toggleLike(id, currentUser.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    // 收藏/取消收藏
    @PostMapping("/{id}/favorite")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        boolean favorited = articleService.toggleFavorite(id, currentUser.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("favorited", favorited);
        return ResponseEntity.ok(response);
    }
}