package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class PostCommentCreateRequest {
    private String content;
    private Long parentId;
}