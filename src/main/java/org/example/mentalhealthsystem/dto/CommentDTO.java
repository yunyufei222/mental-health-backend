package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CommentDTO {
    private Long id;
    private Long articleId;
    private Long parentId;          // 父评论ID
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private Boolean likedByCurrentUser;  // 当前用户是否点赞

    private List<CommentDTO> replies;    // 子回复列表
}