package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CounselorDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String qualification;
    private String expertise;
    private String introduction;
    private BigDecimal price;
    private BigDecimal rating;
    private Integer reviewCount;
    private Integer status;
    private List<ScheduleDTO> schedules;  // 用于详情
}