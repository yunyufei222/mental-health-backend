package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class CommentCreateRequest {
    private String content;
    private Long parentId;  // 可选，如果是回复则传父评论ID
}
