package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostDetailDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String anonymousId;
    private String title;
    private String content;
    private String type;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isAnonymous;
    private Boolean isEssence;
    private LocalDateTime createdAt;
    private Boolean likedByCurrentUser;
    private List<PostCommentDTO> comments; // 顶级评论列表
}