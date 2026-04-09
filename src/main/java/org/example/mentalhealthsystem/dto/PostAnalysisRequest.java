package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class PostAnalysisRequest {
    private String content;
    private Long postId; // 可选，关联的帖子ID
}