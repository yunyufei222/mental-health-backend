package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.ArticleCreateRequest;
import org.example.mentalhealthsystem.entity.Article;
import org.example.mentalhealthsystem.entity.ArticleCategory;
import org.example.mentalhealthsystem.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/articles")
@CrossOrigin(origins = "http://localhost:5174")
@PreAuthorize("hasRole('ADMIN')")
public class AdminArticleController {

    @Autowired
    private ArticleService articleService;

    // ----- 分类管理 -----
    @PostMapping("/categories")
    public ResponseEntity<ArticleCategory> createCategory(@RequestBody ArticleCategory category) {
        return ResponseEntity.ok(articleService.createCategory(category));
    }

    @GetMapping("/categories")
    public ResponseEntity<Page<ArticleCategory>> getAllCategories(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(articleService.getAllCategories(pageable));
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        articleService.deleteCategory(id);
        return ResponseEntity.ok().build();
    }

    // ----- 文章管理 -----
    @PostMapping
    public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.createArticle(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable Long id, @RequestBody ArticleCreateRequest request) {
        return ResponseEntity.ok(articleService.updateArticle(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable Long id) {
        articleService.deleteArticle(id);
        return ResponseEntity.ok().build();
    }
}