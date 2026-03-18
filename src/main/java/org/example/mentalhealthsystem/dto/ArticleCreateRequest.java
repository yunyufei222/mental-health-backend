package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class ArticleCreateRequest {
    private Long categoryId;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private Boolean isTop;
    private Integer status; // 0-草稿，1-发布
    private List<String> tags;  // 标签名称列表
}