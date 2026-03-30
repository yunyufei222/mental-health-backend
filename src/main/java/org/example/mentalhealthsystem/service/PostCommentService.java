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

import java.util.List;
import java.util.Map;
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

    // ---------- 获取评论（树形结构） ----------
    @Transactional(readOnly = true)
    public Page<PostCommentDTO> getComments(Long postId, Pageable pageable, Long currentUserId) {
        // 1. 分页查询顶级评论（parent = null）
        Page<PostComment> topLevelPage = commentRepository.findByPostIdAndParentIsNullOrderByCreatedAtDesc(postId, pageable);
        if (topLevelPage.isEmpty()) {
            return topLevelPage.map(c -> convertToDTO(c, currentUserId, null));
        }

        // 2. 获取所有顶级评论的ID
        List<Long> topLevelIds = topLevelPage.getContent().stream()
                .map(PostComment::getId)
                .collect(Collectors.toList());

        // 3. 批量查询这些顶级评论的所有直接回复
        List<PostComment> allReplies = commentRepository.findByParentIdInOrderByCreatedAtAsc(topLevelIds);

        // 4. 按父评论ID分组
        Map<Long, List<PostComment>> repliesByParentId = allReplies.stream()
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        // 5. 转换为DTO（包含回复）
        List<PostCommentDTO> topLevelDTOs = topLevelPage.getContent().stream()
                .map(comment -> convertToDTO(comment, currentUserId, repliesByParentId.get(comment.getId())))
                .collect(Collectors.toList());

        return new org.springframework.data.domain.PageImpl<>(topLevelDTOs, pageable, topLevelPage.getTotalElements());
    }

    // ---------- 发表评论 ----------
    @Transactional
    public PostCommentDTO createComment(Long postId, Long userId, PostCommentCreateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 敏感词过滤
        if (sensitiveWordService.containsSensitiveWord(request.getContent())) {
            throw new RuntimeException("评论包含敏感词");
        }

        PostComment comment = new PostComment();
        comment.setPost(post);
        comment.setUser(user);
        comment.setContent(request.getContent());
        comment.setStatus(1);
        comment.setLikeCount(0);

        // 处理回复
        if (request.getParentId() != null) {
            PostComment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("父评论不存在"));
            if (!parent.getPost().getId().equals(postId)) {
                throw new RuntimeException("父评论不属于当前帖子");
            }
            comment.setParent(parent);
        }

        PostComment savedComment = commentRepository.save(comment);
        // 更新帖子的评论数
        post.setCommentCount(post.getCommentCount() + 1);
        postRepository.save(post);
        return convertToDTO(savedComment, userId, null);
    }

    // ---------- 删除评论（软删除） ----------
    @Transactional
    public void deleteComment(Long commentId) {
        PostComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        comment.setStatus(0);
        commentRepository.save(comment);
        // 可选：更新帖子的评论数（减一）
        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);
    }

    // 检查评论是否属于某用户
    public boolean isCommentOwner(Long commentId, Long userId) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getUser().getId().equals(userId))
                .orElse(false);
    }

    // ---------- 评论点赞 ----------
    @Transactional
    public void toggleLike(Long commentId, Long userId) {
        // 注意：此功能需要 PostCommentLike 实体和对应的 Repository
        // 实现方式可参考文章评论的 CommentLikeRepository
        // 此处暂时抛出未实现异常，您可根据需要补充完整
        throw new UnsupportedOperationException("评论点赞功能暂未实现，请补充 PostCommentLike 相关代码");
    }

    // ---------- 辅助转换方法 ----------
    private PostCommentDTO convertToDTO(PostComment comment, Long currentUserId, List<PostComment> replies) {
        PostCommentDTO dto = new PostCommentDTO();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setNickname(comment.getUser().getNickname());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        // 当前用户是否已点赞（暂未实现，可先设为 false）
        dto.setLikedByCurrentUser(false);

        if (replies != null && !replies.isEmpty()) {
            dto.setReplies(replies.stream()
                    .map(r -> convertToDTO(r, currentUserId, null))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}