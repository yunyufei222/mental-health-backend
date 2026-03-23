package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class PostUpdateRequest {
    private String title;
    private String content;
    private String tags;
}