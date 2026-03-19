package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.ScaleDTO;
import org.example.mentalhealthsystem.service.ScaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/scales")
@CrossOrigin(origins = "http://localhost:5174")
public class ScaleController {

    @Autowired
    private ScaleService scaleService;

    @GetMapping
    public ResponseEntity<Page<ScaleDTO>> getActiveScales(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        return ResponseEntity.ok(scaleService.getActiveScales(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScaleDTO> getScaleDetail(@PathVariable Long id) {
        ScaleDTO detail = scaleService.getScaleDetail(id);
        return ResponseEntity.ok(detail);
    }
}