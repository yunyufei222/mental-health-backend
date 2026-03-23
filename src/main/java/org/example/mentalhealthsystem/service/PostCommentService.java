package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.PostCommentCreateRequest;
import org.example.mentalhealthsystem.dto.PostCommentDTO;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class PostCommentService {

    @Autowired
    private PostCommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    // 发表评论
    @Transactional
    public PostComment createComment(Long postId, Long userId, PostCommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (sensitiveWordService.containsSensitiveWord(request.getContent())) {
            throw new RuntimeException("评论包含敏感词");
        }

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());

        if (request.getParentId() != null) {
            PostComment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("父评论不存在"));
            comment.setParent(parent);
        }

        comment = commentRepository.save(comment);
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return comment;
    }

    // 删除评论（逻辑删除）
    @Transactional
    public void deleteComment(Long commentId, Long userId) {
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        if (!comment.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除他人评论");
        }
        comment.setStatus(0);
        commentRepository.save(comment);
        // 更新帖子评论数（可能需要遍历子评论，简化处理）
    }

    // 获取帖子评论（分页，树形）
    public Page<PostCommentDTO> getComments(Long postId, Pageable pageable, Long currentUserId) {
        Page<PostComment> page = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId, pageable);
        return page.map(comment -> convertToDTO(comment, currentUserId));
    }

    private PostCommentDTO convertToDTO(PostComment comment, Long currentUserId) {
        PostCommentDTO dto = new PostCommentDTO();
        dto.setId(comment.getId());
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setNickname(comment.getUser().getNickname());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        // 这里简化处理，没有实现评论点赞功能，后续可添加
        dto.setLikedByCurrentUser(false);
        // 获取回复（如果存在）
        if (comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            dto.setReplies(comment.getReplies().stream()
                    .filter(reply -> reply.getStatus() == 1)
                    .map(reply -> convertToDTO(reply, currentUserId))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}