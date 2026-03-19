package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.entity.ArticleCategory;
import org.example.mentalhealthsystem.entity.Tag;
import org.example.mentalhealthsystem.repository.ArticleCategoryRepository;
import org.example.mentalhealthsystem.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicController {

    @Autowired
    private ArticleCategoryRepository categoryRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/categories")
    public ResponseEntity<List<ArticleCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @GetMapping("/tags")
    public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }
}