package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.GratitudeJournalRequest;
import org.example.mentalhealthsystem.dto.GratitudeJournalResponse;
import org.example.mentalhealthsystem.entity.GratitudeJournal;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.repository.GratitudeJournalRepository;
import org.example.mentalhealthsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
public class GratitudeJournalService {

    @Autowired
    private GratitudeJournalRepository journalRepository;

    @Autowired
    private UserRepository userRepository;

    // 获取今天的日记（用于编辑）
    @Transactional(readOnly = true)
    public GratitudeJournalResponse getTodayJournal(Long userId) {
        LocalDate today = LocalDate.now();
        return journalRepository.findByUserIdAndDate(userId, today)
                .map(this::convertToDTO)
                .orElse(null);
    }

    // 创建或更新今天的日记
    @Transactional
    public GratitudeJournalResponse saveTodayJournal(Long userId, GratitudeJournalRequest request) {
        User user = userRepository.getReferenceById(userId);
        LocalDate today = LocalDate.now();
        GratitudeJournal journal = journalRepository.findByUserIdAndDate(userId, today)
                .orElse(new GratitudeJournal());
        journal.setUser(user);
        journal.setDate(today);
        journal.setItem1(request.getItem1());
        journal.setItem2(request.getItem2());
        journal.setItem3(request.getItem3());
        journal.setReflection(request.getReflection());
        journal = journalRepository.save(journal);
        return convertToDTO(journal);
    }

    // 分页获取历史日记
    @Transactional(readOnly = true)
    public Page<GratitudeJournalResponse> getHistory(Long userId, Pageable pageable) {
        Page<GratitudeJournal> page = journalRepository.findByUserIdOrderByDateDesc(userId, pageable);
        return page.map(this::convertToDTO);
    }

    private GratitudeJournalResponse convertToDTO(GratitudeJournal journal) {
        GratitudeJournalResponse dto = new GratitudeJournalResponse();
        dto.setId(journal.getId());
        dto.setDate(journal.getDate());
        dto.setItem1(journal.getItem1());
        dto.setItem2(journal.getItem2());
        dto.setItem3(journal.getItem3());
        dto.setReflection(journal.getReflection());
        dto.setCreatedAt(journal.getCreatedAt());
        dto.setUpdatedAt(journal.getUpdatedAt());
        return dto;
    }
}