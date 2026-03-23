package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.PostCreateRequest;
import org.example.mentalhealthsystem.dto.PostDTO;
import org.example.mentalhealthsystem.dto.PostDetailDTO;
import org.example.mentalhealthsystem.dto.PostUpdateRequest;
import org.example.mentalhealthsystem.entity.Post;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.service.PostService;
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
@RequestMapping("/api/community/posts")
@CrossOrigin(origins = "http://localhost:5174")
public class PostController {

    @Autowired
    private PostService postService;

    /**
     * 获取帖子列表（支持类型筛选）
     */
    @GetMapping
    public ResponseEntity<Page<PostDTO>> getPosts(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User currentUser) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Long userId = currentUser != null ? currentUser.getId() : null;
        Page<PostDTO> posts = postService.getPosts(type, pageable, userId);
        return ResponseEntity.ok(posts);
    }

    /**
     * 获取帖子详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<PostDetailDTO> getPostDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        Long userId = currentUser != null ? currentUser.getId() : null;
        PostDetailDTO detail = postService.getPostDetail(id, userId);
        return ResponseEntity.ok(detail);
    }

    /**
     * 创建帖子（需要登录）
     */
    @PostMapping
    public ResponseEntity<Post> createPost(
            @RequestBody PostCreateRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Post post = postService.createPost(currentUser.getId(), request);
        return ResponseEntity.ok(post);
    }

    /**
     * 更新自己的帖子
     */
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(
            @PathVariable Long id,
            @RequestBody PostUpdateRequest request,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        Post post = postService.updatePost(id, currentUser.getId(), request);
        return ResponseEntity.ok(post);
    }

    /**
     * 删除自己的帖子（软删除）
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        postService.deletePost(id, currentUser.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * 点赞/取消点赞帖子
     */
    @PostMapping("/{id}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        boolean liked = postService.toggleLike(id, currentUser.getId());
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }

    /**
     * 优势标签点赞
     */
    @PostMapping("/{id}/strength")
    public ResponseEntity<Map<String, Object>> toggleStrength(
            @PathVariable Long id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal User currentUser) {
        if (currentUser == null) {
            return ResponseEntity.status(401).build();
        }
        String strength = payload.get("strength");
        if (strength == null || strength.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        boolean liked = postService.toggleStrength(id, currentUser.getId(), strength);
        Map<String, Object> response = new HashMap<>();
        response.put("liked", liked);
        return ResponseEntity.ok(response);
    }
}