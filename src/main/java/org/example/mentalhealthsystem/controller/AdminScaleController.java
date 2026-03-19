package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.OptionDTO;
import org.example.mentalhealthsystem.dto.QuestionDTO;
import org.example.mentalhealthsystem.dto.ScaleDTO;
import org.example.mentalhealthsystem.entity.Option;
import org.example.mentalhealthsystem.entity.Question;
import org.example.mentalhealthsystem.entity.Scale;
import org.example.mentalhealthsystem.service.ScaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/scales")
@CrossOrigin(origins = "http://localhost:5174")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScaleController {

    @Autowired
    private ScaleService scaleService;

    @GetMapping
    public ResponseEntity<Page<ScaleDTO>> getAllScales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return ResponseEntity.ok(scaleService.getAllScales(pageable));
    }

    @PostMapping
    public ResponseEntity<Scale> createScale(@RequestBody ScaleDTO scaleDTO) {
        return ResponseEntity.ok(scaleService.createScale(scaleDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Scale> updateScale(@PathVariable Long id, @RequestBody ScaleDTO scaleDTO) {
        return ResponseEntity.ok(scaleService.updateScale(id, scaleDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteScale(@PathVariable Long id) {
        scaleService.deleteScale(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{scaleId}/questions")
    public ResponseEntity<Question> addQuestion(@PathVariable Long scaleId, @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(scaleService.addQuestion(scaleId, questionDTO));
    }

    @PutMapping("/questions/{questionId}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long questionId, @RequestBody QuestionDTO questionDTO) {
        return ResponseEntity.ok(scaleService.updateQuestion(questionId, questionDTO));
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long questionId) {
        scaleService.deleteQuestion(questionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/options")
    public ResponseEntity<Option> addOption(@PathVariable Long questionId, @RequestBody OptionDTO optionDTO) {
        return ResponseEntity.ok(scaleService.addOption(questionId, optionDTO));
    }

    @PutMapping("/options/{optionId}")
    public ResponseEntity<Option> updateOption(@PathVariable Long optionId, @RequestBody OptionDTO optionDTO) {
        return ResponseEntity.ok(scaleService.updateOption(optionId, optionDTO));
    }

    @DeleteMapping("/options/{optionId}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long optionId) {
        scaleService.deleteOption(optionId);
        return ResponseEntity.ok().build();
    }
}