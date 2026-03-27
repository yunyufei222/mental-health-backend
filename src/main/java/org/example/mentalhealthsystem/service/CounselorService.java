package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.CounselorDTO;
import org.example.mentalhealthsystem.dto.ScheduleDTO;
import org.example.mentalhealthsystem.entity.Counselor;
import org.example.mentalhealthsystem.entity.CounselorSchedule;
import org.example.mentalhealthsystem.repository.CounselorRepository;
import org.example.mentalhealthsystem.repository.CounselorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CounselorService {

    @Autowired
    private CounselorRepository counselorRepository;

    @Autowired
    private CounselorScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Page<CounselorDTO> getActiveCounselors(Pageable pageable) {
        Page<Counselor> page = counselorRepository.findByStatus(1, pageable);
        return page.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public CounselorDTO getCounselorDetail(Long id) {
        Counselor counselor = counselorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("咨询师不存在"));
        if (counselor.getStatus() != 1) {
            throw new RuntimeException("咨询师已停用");
        }
        return convertToDetailDTO(counselor);
    }

    @Transactional(readOnly = true)
    public List<ScheduleDTO> getAvailableSchedules(Long counselorId, LocalDate date) {
        List<CounselorSchedule> schedules = scheduleRepository.findByCounselorIdAndDateAndIsBookedFalse(counselorId, date);
        return schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList());
    }

    private CounselorDTO convertToDTO(Counselor counselor) {
        CounselorDTO dto = new CounselorDTO();
        dto.setId(counselor.getId());
        dto.setUserId(counselor.getUser().getId());
        dto.setUsername(counselor.getUser().getUsername());
        dto.setNickname(counselor.getUser().getNickname());
        dto.setAvatar(counselor.getUser().getAvatar());
        dto.setQualification(counselor.getQualification());
        dto.setExpertise(counselor.getExpertise());
        dto.setIntroduction(counselor.getIntroduction());
        dto.setPrice(counselor.getPrice());
        dto.setRating(counselor.getRating());
        dto.setReviewCount(counselor.getReviewCount());
        dto.setStatus(counselor.getStatus());
        return dto;
    }

    private CounselorDTO convertToDetailDTO(Counselor counselor) {
        CounselorDTO dto = convertToDTO(counselor);
        // 获取未来可预约时段（简化：获取所有未预约的排班，实际可加日期范围）
        List<CounselorSchedule> schedules = scheduleRepository.findByCounselorAndDateAfterOrderByDateAscStartTimeAsc(counselor, LocalDate.now());
        dto.setSchedules(schedules.stream()
                .map(this::convertToScheduleDTO)
                .collect(Collectors.toList()));
        return dto;
    }

    private ScheduleDTO convertToScheduleDTO(CounselorSchedule schedule) {
        ScheduleDTO dto = new ScheduleDTO();
        dto.setId(schedule.getId());
        dto.setDate(schedule.getDate());
        dto.setStartTime(schedule.getStartTime());
        dto.setEndTime(schedule.getEndTime());
        dto.setIsBooked(schedule.getIsBooked());
        return dto;
    }
}