package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.CommentDTO;
import org.example.mentalhealthsystem.dto.CommentCreateRequest;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/articles/{articleId}/comments")
@CrossOrigin(origins = "http://localhost:5174")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 获取文章的顶级评论列表（分页，包含回复）
    @GetMapping
    public ResponseEntity<Page<CommentDTO>> getComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<CommentDTO> comments = commentService.getTopLevelComments(articleId, pageable, userId);
        return ResponseEntity.ok(comments);
    }

    // 发表评论（可包含 parentId）
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @PathVariable Long articleId,
            @RequestBody CommentCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        CommentDTO comment = commentService.createComment(articleId, currentUser.getId(), request);
        return ResponseEntity.ok(comment);
    }

    // 删除自己的评论
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        if (!commentService.isCommentOwner(commentId, currentUser.getId())) {
            return ResponseEntity.status(403).build();
        }
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

    // 点赞/取消点赞评论
    @PostMapping("/{commentId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long articleId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        boolean liked = commentService.toggleLike(commentId, currentUser.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }
}