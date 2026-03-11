package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDetailDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String summary;
    private String content;
    private String coverImage;
    private String author;
    private Integer viewCount;
    private Integer likeCount;
    private Integer favoriteCount;
    private Boolean isTop;
    private LocalDateTime publishedAt;
    private Boolean likedByCurrentUser;
    private Boolean favoritedByCurrentUser;
}