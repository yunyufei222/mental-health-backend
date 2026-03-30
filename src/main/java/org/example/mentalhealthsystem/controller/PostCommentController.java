package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.PostCommentCreateRequest;
import org.example.mentalhealthsystem.dto.PostCommentDTO;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.PostCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/community/posts/{postId}/comments")
@CrossOrigin(origins = "http://localhost:5174")
public class PostCommentController {

    @Autowired
    private PostCommentService postCommentService;

    // 获取帖子的顶级评论列表（分页）
    @GetMapping
    public ResponseEntity<Page<PostCommentDTO>> getComments(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<PostCommentDTO> comments = postCommentService.getComments(postId, pageable, userId);
        return ResponseEntity.ok(comments);
    }

    // 发表评论（可带 parentId）
    @PostMapping
    public ResponseEntity<PostCommentDTO> createComment(
            @PathVariable Long postId,
            @RequestBody PostCommentCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        PostCommentDTO comment = postCommentService.createComment(postId, currentUser.getId(), request);
        return ResponseEntity.ok(comment);
    }

    // 删除自己的评论
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        if (!postCommentService.isCommentOwner(commentId, currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        postCommentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 点赞评论（可选，如果后端已实现）
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Void> likeComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        postCommentService.toggleLike(commentId, currentUser.getId()); // 需要实现
        return ResponseEntity.ok().build();
    }
}