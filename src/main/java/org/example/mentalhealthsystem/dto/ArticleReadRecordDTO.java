package org.example.mentalhealthsystem.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ArticleReadRecordDTO {
    private Long id;
    private Long articleId;
    private String articleTitle;
    private String articleSummary;
    private String articleCover;
    private LocalDateTime readAt;
}