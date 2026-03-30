package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostCommentDTO {
    private Long id;
    private Long postId;          // 新增
    private Long parentId;        // 新增
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String content;
    private Integer likeCount;
    private LocalDateTime createdAt;
    private Boolean likedByCurrentUser;
    private List<PostCommentDTO> replies;
}