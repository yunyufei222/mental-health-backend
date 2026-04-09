package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.AppointmentDTO;
import org.example.mentalhealthsystem.dto.AppointmentRequest;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.AppointmentRepository;
import org.example.mentalhealthsystem.repository.CounselorScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private CounselorScheduleRepository scheduleRepository;

    @Transactional
    public Appointment createAppointment(Long userId, AppointmentRequest request) {
        CounselorSchedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new RuntimeException("排班不存在"));
        if (schedule.getIsBooked()) {
            throw new RuntimeException("该时段已被预约");
        }

        Counselor counselor = schedule.getCounselor();
        if (counselor.getStatus() != 1) {
            throw new RuntimeException("咨询师不可用");
        }

        User user = new User();
        user.setId(userId);

        Appointment appointment = new Appointment();
        appointment.setUser(user);
        appointment.setCounselor(counselor);
        appointment.setSchedule(schedule);
        appointment.setAppointmentDate(schedule.getDate());
        appointment.setStartTime(schedule.getStartTime());
        appointment.setEndTime(schedule.getEndTime());
        appointment.setProblemDesc(request.getProblemDesc());
        if (request.getConsultationType() != null) {
            try {
                appointment.setConsultationType(Appointment.ConsultationType.valueOf(request.getConsultationType()));
            } catch (IllegalArgumentException e) {
                appointment.setConsultationType(Appointment.ConsultationType.ONLINE);
            }
        }
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        schedule.setIsBooked(true);
        scheduleRepository.save(schedule);

        return appointmentRepository.save(appointment);
    }

    @Transactional
    public void cancelAppointment(Long appointmentId, Long userId, boolean isAdmin) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        if (!isAdmin && !appointment.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权取消他人预约");
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("已完成咨询无法取消");
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("预约已取消");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CANCELLED);
        CounselorSchedule schedule = appointment.getSchedule();
        schedule.setIsBooked(false);
        scheduleRepository.save(schedule);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment confirmAppointment(Long appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("只有待确认的预约才能确认");
        }

        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment completeAppointment(Long appointmentId, String feedback) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));

        if (appointment.getStatus() != Appointment.AppointmentStatus.CONFIRMED) {
            throw new RuntimeException("只有已确认的预约才能标记为完成");
        }

        appointment.setStatus(Appointment.AppointmentStatus.COMPLETED);
        if (feedback != null && !feedback.trim().isEmpty()) {
            appointment.setFeedback(feedback);
        }
        return appointmentRepository.save(appointment);
    }

    @Transactional
    public Appointment updateAppointmentStatus(Long appointmentId, String status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        Appointment.AppointmentStatus newStatus;
        try {
            newStatus = Appointment.AppointmentStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("无效的状态值");
        }

        if (appointment.getStatus() == Appointment.AppointmentStatus.COMPLETED) {
            throw new RuntimeException("已完成咨询无法修改状态");
        }
        if (appointment.getStatus() == Appointment.AppointmentStatus.CANCELLED) {
            throw new RuntimeException("已取消的预约无法修改");
        }

        Appointment.AppointmentStatus oldStatus = appointment.getStatus();
        appointment.setStatus(newStatus);

        if (newStatus == Appointment.AppointmentStatus.CANCELLED && oldStatus != Appointment.AppointmentStatus.CANCELLED) {
            CounselorSchedule schedule = appointment.getSchedule();
            schedule.setIsBooked(false);
            scheduleRepository.save(schedule);
        }

        return appointmentRepository.save(appointment);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getUserAppointments(Long userId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return page.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getCounselorAppointments(Long counselorId, Pageable pageable) {
        Page<Appointment> page;
        if (counselorId == null) {
            page = appointmentRepository.findAll(pageable);
        } else {
            page = appointmentRepository.findByCounselorIdOrderByCreatedAtDesc(counselorId, pageable);
        }
        return page.map(this::convertToDTO);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentDTO> getCounselorAppointmentsByCounselorId(Long counselorId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByCounselorIdOrderByCreatedAtDesc(counselorId, pageable);
        return page.map(this::convertToDTO);
    }

    private AppointmentDTO convertToDTO(Appointment appointment) {
        AppointmentDTO dto = new AppointmentDTO();
        dto.setId(appointment.getId());
        dto.setCounselorId(appointment.getCounselor().getId());
        dto.setCounselorName(appointment.getCounselor().getUser().getNickname());
        dto.setCounselorAvatar(appointment.getCounselor().getUser().getAvatar());
        dto.setAppointmentDate(appointment.getAppointmentDate());
        dto.setStartTime(appointment.getStartTime());
        dto.setEndTime(appointment.getEndTime());
        dto.setProblemDesc(appointment.getProblemDesc());
        dto.setConsultationType(appointment.getConsultationType().name());
        dto.setStatus(appointment.getStatus().name());
        dto.setFeedback(appointment.getFeedback());
        dto.setRating(appointment.getRating());
        dto.setCreatedAt(appointment.getCreatedAt());
        return dto;
    }
    @Transactional
    public Appointment confirmAppointmentByCounselor(Long appointmentId, Long counselorId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("预约不存在"));
        if (!appointment.getCounselor().getId().equals(counselorId)) {
            throw new RuntimeException("无权操作其他咨询师的预约");
        }
        if (appointment.getStatus() != Appointment.AppointmentStatus.PENDING) {
            throw new RuntimeException("只有待确认的预约才能确认");
        }
        // 更新预约状态
        appointment.setStatus(Appointment.AppointmentStatus.CONFIRMED);
        // 排班已在创建预约时标记为已预约，无需再次检查或修改
        return appointmentRepository.save(appointment);
    }
}