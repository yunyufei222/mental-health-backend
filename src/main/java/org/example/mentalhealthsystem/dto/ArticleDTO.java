package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

import java.util.Set;

@Data
public class ArticleDTO {
    private Long id;
    private String categoryName;
    private String title;
    private String summary;
    private String coverImage;
    private String author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Boolean isTop;
    private LocalDateTime publishedAt;
    // 当前用户是否点赞/收藏（可选，通过额外字段填充）
    private Boolean likedByCurrentUser;
    private Boolean favoritedByCurrentUser;
    private Set<String> tags;  // 存放标签名称 // 标签名称列表
}