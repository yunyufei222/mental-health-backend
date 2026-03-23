package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class PostCreateRequest {
    private String title;
    private String content;
    private String type; // SHARE, ASK, GRATITUDE, GROWTH
    private String tags; // 逗号分隔
    private Boolean isAnonymous;
}