package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDTO {
    private Long id;
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String anonymousId;
    private String title;
    private String summary;
    private String type;
    private String tags;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean isAnonymous;
    private Boolean isEssence;
    private Integer status;
    private LocalDateTime createdAt;
    private Boolean likedByCurrentUser;
}