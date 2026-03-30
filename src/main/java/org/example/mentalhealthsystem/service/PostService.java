package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.*;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostLikeRepository likeRepository;

    @Autowired
    private PostStrengthRepository strengthRepository;

    @Autowired
    private PostCommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AnonymousService anonymousService;

    @Autowired
    private SensitiveWordService sensitiveWordService;

    // 创建帖子
    @Transactional
    public Post createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 敏感词过滤
        if (sensitiveWordService.containsSensitiveWord(request.getTitle()) ||
                sensitiveWordService.containsSensitiveWord(request.getContent())) {
            throw new RuntimeException("内容包含敏感词，请修改后重新发布");
        }

        Post post = new Post();
        post.setUser(user);
        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setType(Post.PostType.valueOf(request.getType()));
        post.setTags(request.getTags());
        post.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : false);

        // 匿名处理
        if (post.getIsAnonymous()) {
            if (user.getAnonymousId() == null) {
                // 为用户生成一个固定的匿名ID
                String anonymousId = anonymousService.generateAnonymousId();
                user.setAnonymousId(anonymousId);
                userRepository.save(user);
            }
            post.setAnonymousId(user.getAnonymousId());
        }

        // 默认状态为1（正常），如果需要审核则设为2（待审核）
        post.setStatus(1); // 暂不开启审核

        return postRepository.save(post);
    }

    // 更新帖子
    @Transactional
    public Post updatePost(Long postId, Long userId, PostUpdateRequest request) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权修改他人帖子");
        }

        if (sensitiveWordService.containsSensitiveWord(request.getTitle()) ||
                sensitiveWordService.containsSensitiveWord(request.getContent())) {
            throw new RuntimeException("内容包含敏感词，请修改后重新发布");
        }

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setTags(request.getTags());
        return postRepository.save(post);
    }

    // 删除帖子（逻辑删除）
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (!post.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权删除他人帖子");
        }
        post.setStatus(0); // 软删除
        postRepository.save(post);
    }

    // 管理员删除帖子
    @Transactional
    public void adminDeletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        post.setStatus(0);
        postRepository.save(post);
    }

    // 获取帖子列表（用户端）
    @Transactional(readOnly = true)
    public Page<PostDTO> getPosts(String type, Pageable pageable, Long currentUserId) {
        Post.PostType postType = type != null ? Post.PostType.valueOf(type) : null;
        Page<Post> page = postRepository.findPublishedPosts(postType, pageable);
        return page.map(post -> convertToDTO(post, currentUserId));
    }

    // 获取帖子详情（修正版）
    @Transactional
    public PostDetailDTO getPostDetail(Long postId, Long currentUserId) {
        // 先增加阅读数（原子操作，不依赖实体管理器）
        postRepository.incrementViewCount(postId);

        // 重新查询帖子（确保获取最新的 viewCount）
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        if (post.getStatus() != 1) {
            throw new RuntimeException("帖子不存在或已删除");
        }
        return convertToDetailDTO(post, currentUserId);
    }

    // 点赞/取消点赞帖子
    @Transactional
    public boolean toggleLike(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (likeRepository.existsByPostAndUser(post, user)) {
            likeRepository.deleteByPostIdAndUserId(postId, userId);
            post.setLikeCount(post.getLikeCount() - 1);
            return false;
        } else {
            PostLike like = new PostLike();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            post.setLikeCount(post.getLikeCount() + 1);
            return true;
        }
    }

    // 优势标签点赞
    @Transactional
    public boolean toggleStrength(Long postId, Long userId, String strength) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("帖子不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        if (strengthRepository.existsByPostAndUserAndStrength(post, user, strength)) {
            strengthRepository.deleteByPostIdAndUserIdAndStrength(postId, userId, strength);
            return false;
        } else {
            PostStrength ps = new PostStrength();
            ps.setPost(post);
            ps.setUser(user);
            ps.setStrength(strength);
            strengthRepository.save(ps);
            return true;
        }
    }

    // 获取帖子中各个优势标签的点赞数
    public List<PostStrengthDTO> getStrengthCounts(Long postId) {
        // 可以自定义查询，这里简化：从strengthRepository统计
        // 为了简单，此处省略实现，您可以在Repository中添加统计方法
        return List.of();
    }

    // 转换方法
    private PostDTO convertToDTO(Post post, Long currentUserId) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        if (post.getIsAnonymous()) {
            dto.setUsername("匿名用户");
            dto.setNickname("匿名");
            dto.setAvatar(null);
            dto.setAnonymousId(post.getAnonymousId());
        } else {
            dto.setUsername(post.getUser().getUsername());
            dto.setNickname(post.getUser().getNickname());
            dto.setAvatar(post.getUser().getAvatar());
        }
        dto.setTitle(post.getTitle());
        dto.setSummary(post.getContent().length() > 100 ? post.getContent().substring(0, 100) + "..." : post.getContent());
        dto.setType(post.getType().name());
        dto.setTags(post.getTags());
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setIsAnonymous(post.getIsAnonymous());
        dto.setIsEssence(post.getIsEssence());
        dto.setStatus(post.getStatus());
        dto.setCreatedAt(post.getCreatedAt());
        if (currentUserId != null) {
            Post postProxy = new Post();
            postProxy.setId(post.getId());
            User userProxy = new User();
            userProxy.setId(currentUserId);
            dto.setLikedByCurrentUser(likeRepository.existsByPostAndUser(postProxy, userProxy));
        }
        return dto;
    }

    private PostDetailDTO convertToDetailDTO(Post post, Long currentUserId) {
        PostDetailDTO dto = new PostDetailDTO();
        dto.setId(post.getId());
        dto.setUserId(post.getUser().getId());
        if (post.getIsAnonymous()) {
            dto.setUsername("匿名用户");
            dto.setNickname("匿名");
            dto.setAvatar(null);
            dto.setAnonymousId(post.getAnonymousId());
        } else {
            dto.setUsername(post.getUser().getUsername());
            dto.setNickname(post.getUser().getNickname());
            dto.setAvatar(post.getUser().getAvatar());
        }
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setType(post.getType().name());
        dto.setTags(post.getTags());
        dto.setViewCount(post.getViewCount());
        dto.setLikeCount(post.getLikeCount());
        dto.setCommentCount(post.getCommentCount());
        dto.setIsAnonymous(post.getIsAnonymous());
        dto.setIsEssence(post.getIsEssence());
        dto.setCreatedAt(post.getCreatedAt());
        if (currentUserId != null) {
            Post postProxy = new Post();
            postProxy.setId(post.getId());
            User userProxy = new User();
            userProxy.setId(currentUserId);
            dto.setLikedByCurrentUser(likeRepository.existsByPostAndUser(postProxy, userProxy));
        }
        // 评论列表（顶级）将在CommentService中获取，这里暂时置空
        dto.setComments(List.of());
        return dto;
    }
}