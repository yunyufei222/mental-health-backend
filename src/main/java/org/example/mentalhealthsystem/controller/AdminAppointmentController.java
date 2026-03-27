package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.AppointmentDTO;
import org.example.mentalhealthsystem.dto.AppointmentStatusUpdateRequest;
import org.example.mentalhealthsystem.entity.Appointment;
import org.example.mentalhealthsystem.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/appointments")
@CrossOrigin(origins = "http://localhost:5174")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    /**
     * 获取所有预约（分页，可选按咨询师筛选）
     */
    @GetMapping
    public ResponseEntity<Page<AppointmentDTO>> getAllAppointments(
            @RequestParam(required = false) Long counselorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (counselorId != null) {
            return ResponseEntity.ok(appointmentService.getCounselorAppointments(counselorId, pageable));
        }
        // 如果需要获取所有预约，可以添加 getAllAppointments 方法，这里暂用 getCounselorAppointments 的变体
        // 建议在 Service 中添加 getAllAppointments 方法
        return ResponseEntity.ok(appointmentService.getCounselorAppointments(null, pageable));
    }

    /**
     * 获取指定咨询师的预约列表
     */
    @GetMapping("/counselor/{counselorId}")
    public ResponseEntity<Page<AppointmentDTO>> getCounselorAppointments(
            @PathVariable Long counselorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(appointmentService.getCounselorAppointments(counselorId, pageable));
    }

    /**
     * 确认预约（将 PENDING 改为 CONFIRMED）
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<Map<String, Object>> confirmAppointment(@PathVariable Long id) {
        Appointment appointment = appointmentService.confirmAppointment(id);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "预约已确认");
        response.put("appointment", appointment);
        return ResponseEntity.ok(response);
    }

    /**
     * 完成咨询（将 CONFIRMED 改为 COMPLETED）
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<Map<String, Object>> completeAppointment(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> requestBody) {
        String feedback = requestBody != null ? requestBody.get("feedback") : null;
        Appointment appointment = appointmentService.completeAppointment(id, feedback);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "咨询已完成");
        response.put("appointment", appointment);
        return ResponseEntity.ok(response);
    }

    /**
     * 取消预约（管理员取消，需释放排班）
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Map<String, Object>> cancelAppointment(@PathVariable Long id) {
        // 管理员取消预约，传入 true 表示是管理员操作
        appointmentService.cancelAppointment(id, null, true);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "预约已取消");
        return ResponseEntity.ok(response);
    }

    /**
     * 通用状态更新（支持 PENDING, CONFIRMED, COMPLETED, CANCELLED）
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> updateAppointmentStatus(
            @PathVariable Long id,
            @RequestBody AppointmentStatusUpdateRequest request) {
        Appointment appointment = appointmentService.updateAppointmentStatus(id, request.getStatus());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "状态已更新");
        response.put("appointment", appointment);
        return ResponseEntity.ok(response);
    }
}