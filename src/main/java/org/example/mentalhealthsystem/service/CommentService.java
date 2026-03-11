package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.CommentDTO;
import org.example.mentalhealthsystem.dto.CommentCreateRequest;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    // ---------- 评论获取 ----------
    /**
     * 分页获取文章的顶级评论，并填充每个顶级评论的直接回复
     */
    @Transactional(readOnly = true)
    public Page<CommentDTO> getTopLevelComments(Long articleId, Pageable pageable, Long currentUserId) {
        // 1. 分页查询顶级评论
        Page<Comment> topLevelPage = commentRepository.findByArticleIdAndParentIsNullAndStatusOrderByCreatedAtDesc(
                articleId, 1, pageable);

        // 2. 获取所有顶级评论的ID
        List<Long> topLevelIds = topLevelPage.getContent().stream()
                .map(Comment::getId)
                .collect(Collectors.toList());

        // 3. 批量查询这些顶级评论的所有直接回复（按时间升序）
        List<Comment> allReplies = commentRepository.findByParentIdInAndStatusOrderByCreatedAtAsc(topLevelIds, 1);

        // 4. 按父评论ID分组
        Map<Long, List<Comment>> repliesByParentId = allReplies.stream()
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        // 5. 转换为DTO（包含回复）
        List<CommentDTO> topLevelDTOs = topLevelPage.getContent().stream()
                .map(comment -> convertToDTO(comment, currentUserId, repliesByParentId.get(comment.getId())))
                .collect(Collectors.toList());

        return new PageImpl<>(topLevelDTOs, pageable, topLevelPage.getTotalElements());
    }

    /**
     * 获取文章的所有评论（不分页，组装成树），可选用于管理端
     */
    @Transactional(readOnly = true)
    public List<CommentDTO> getAllCommentsTree(Long articleId, Long currentUserId) {
        List<Comment> allComments = commentRepository.findByArticleIdAndStatus(articleId, 1);
        // 构建 parentId -> List<Comment> 映射
        Map<Long, List<Comment>> childrenMap = allComments.stream()
                .filter(c -> c.getParent() != null)
                .collect(Collectors.groupingBy(c -> c.getParent().getId()));

        return allComments.stream()
                .filter(c -> c.getParent() == null)  // 只返回顶级评论
                .map(c -> convertToDTO(c, currentUserId, childrenMap.get(c.getId())))
                .collect(Collectors.toList());
    }

    // ---------- 发表评论 ----------
    @Transactional
    public CommentDTO createComment(Long articleId, Long userId, CommentCreateRequest request) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Comment comment = new Comment();
        comment.setArticle(article);
        comment.setUser(user);
        comment.setContent(request.getContent());
        comment.setStatus(1);
        comment.setLikeCount(0);

        // 处理回复
        if (request.getParentId() != null) {
            Comment parent = commentRepository.findById(request.getParentId())
                    .orElseThrow(() -> new RuntimeException("父评论不存在"));
            // 确保父评论属于同一篇文章
            if (!parent.getArticle().getId().equals(articleId)) {
                throw new RuntimeException("父评论不属于当前文章");
            }
            comment.setParent(parent);
        }

        Comment savedComment = commentRepository.save(comment);
        return convertToDTO(savedComment, userId, null);
    }

    // ---------- 删除评论 ----------
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new RuntimeException("评论不存在");
        }
        commentRepository.deleteById(commentId); // 物理删除，级联删除回复和点赞
    }

    // 检查评论是否属于某用户
    public boolean isCommentOwner(Long commentId, Long userId) {
        return commentRepository.findById(commentId)
                .map(comment -> comment.getUser().getId().equals(userId))
                .orElse(false);
    }

    // ---------- 评论点赞 ----------
    @Transactional
    public boolean toggleLike(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (commentLikeRepository.existsByCommentAndUser(comment, user)) {
            // 取消点赞
            commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
            comment.setLikeCount(comment.getLikeCount() - 1);
            return false;
        } else {
            // 点赞
            CommentLike like = new CommentLike();
            like.setComment(comment);
            like.setUser(user);
            commentLikeRepository.save(like);
            comment.setLikeCount(comment.getLikeCount() + 1);
            return true;
        }
    }

    // 检查用户是否点赞了某评论
    public boolean isLikedByUser(Long commentId, Long userId) {
        if (userId == null) return false;
        Comment commentProxy = new Comment();
        commentProxy.setId(commentId);
        User userProxy = new User();
        userProxy.setId(userId);
        return commentLikeRepository.existsByCommentAndUser(commentProxy, userProxy);
    }

    // ---------- 辅助转换方法 ----------
    private CommentDTO convertToDTO(Comment comment, Long currentUserId, List<Comment> replies) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setArticleId(comment.getArticle().getId());
        dto.setParentId(comment.getParent() != null ? comment.getParent().getId() : null);
        dto.setUserId(comment.getUser().getId());
        dto.setUsername(comment.getUser().getUsername());
        dto.setNickname(comment.getUser().getNickname());
        dto.setAvatar(comment.getUser().getAvatar());
        dto.setContent(comment.getContent());
        dto.setLikeCount(comment.getLikeCount());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setLikedByCurrentUser(isLikedByUser(comment.getId(), currentUserId));

        if (replies != null) {
            dto.setReplies(replies.stream()
                    .map(r -> convertToDTO(r, currentUserId, null))
                    .collect(Collectors.toList()));
        }
        return dto;
    }
}