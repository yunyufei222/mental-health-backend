package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.entity.SensitiveWord;
import org.example.mentalhealthsystem.repository.SensitiveWordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensitiveWordService {

    @Autowired
    private SensitiveWordRepository sensitiveWordRepository;

    private List<String> sensitiveWords;

    @PostConstruct
    public void loadSensitiveWords() {
        sensitiveWords = sensitiveWordRepository.findAll().stream()
                .map(SensitiveWord::getWord)
                .collect(Collectors.toList());
    }

    public boolean containsSensitiveWord(String text) {
        if (text == null || text.isEmpty()) return false;
        for (String word : sensitiveWords) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    // 可选：刷新词库
    public void refresh() {
        loadSensitiveWords();
    }
}