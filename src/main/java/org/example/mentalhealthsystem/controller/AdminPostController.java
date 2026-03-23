package org.example.mentalhealthsystem.controller;

import org.example.mentalhealthsystem.dto.PostDTO;
import org.example.mentalhealthsystem.entity.Post;
import org.example.mentalhealthsystem.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/community")
@CrossOrigin(origins = "http://localhost:5174")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPostController {

    @Autowired
    private PostService postService;

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<Void> adminDeletePost(@PathVariable Long id) {
        postService.adminDeletePost(id);
        return ResponseEntity.ok().build();
    }

    // 获取待审核帖子列表（可根据status筛选）
    @GetMapping("/posts/pending")
    public ResponseEntity<Page<PostDTO>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // 需要实现根据status查询的方法，此处仅示例
        // Page<Post> posts = postRepository.findByStatus(2, pageable);
        // return ResponseEntity.ok(posts.map(post -> ...));
        return ResponseEntity.ok(Page.empty());
    }

    // 审核通过
    @PutMapping("/posts/{id}/approve")
    public ResponseEntity<Void> approvePost(@PathVariable Long id) {
        // 实现审核通过逻辑
        return ResponseEntity.ok().build();
    }

    // 审核驳回
    @PutMapping("/posts/{id}/reject")
    public ResponseEntity<Void> rejectPost(@PathVariable Long id,
                                           @RequestParam String reason) {
        // 实现驳回逻辑
        return ResponseEntity.ok().build();
    }
}