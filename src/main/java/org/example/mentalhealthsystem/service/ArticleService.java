package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.*;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private ArticleCategoryRepository categoryRepository;

    @Autowired
    private ArticleLikeRepository likeRepository;

    @Autowired
    private ArticleFavoriteRepository favoriteRepository;

    @Autowired
    private TagRepository tagRepository;

    // ----- 分类管理 -----
    public ArticleCategory createCategory(ArticleCategory category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("分类名称已存在");
        }
        return categoryRepository.save(category);
    }

    public Page<ArticleCategory> getAllCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable);
    }

    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    // ----- 文章管理（管理员）-----
    @Transactional
    public Article createArticle(ArticleCreateRequest request) {
        Article article = new Article();
        updateArticleFromRequest(article, request);
        // 处理标签
        if (request.getTags() != null) {
            Set<Tag> tags = new HashSet<>();
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
            article.setTags(tags);
        }
        if (article.getStatus() == 1) { // 发布时设置发布时间
            article.setPublishedAt(LocalDateTime.now());
        }
        return articleRepository.save(article);
    }

    @Transactional
    public Article updateArticle(Long id, ArticleCreateRequest request) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        updateArticleFromRequest(article, request);
        // 更新标签
        Set<Tag> tags = new HashSet<>();
        if (request.getTags() != null) {
            for (String tagName : request.getTags()) {
                Tag tag = tagRepository.findByName(tagName)
                        .orElseGet(() -> {
                            Tag newTag = new Tag();
                            newTag.setName(tagName);
                            return tagRepository.save(newTag);
                        });
                tags.add(tag);
            }
        }
        article.setTags(tags);
        if (article.getStatus() == 1 && article.getPublishedAt() == null) {
            article.setPublishedAt(LocalDateTime.now());
        }
        return articleRepository.save(article);
    }

    private void updateArticleFromRequest(Article article, ArticleCreateRequest request) {
        if (request.getCategoryId() != null) {
            ArticleCategory category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }
        article.setTitle(request.getTitle());
        article.setSummary(request.getSummary());
        article.setContent(request.getContent());
        article.setCoverImage(request.getCoverImage());
        article.setAuthor(request.getAuthor());
        article.setIsTop(request.getIsTop());
        article.setStatus(request.getStatus());
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    // ----- 用户端接口 -----
    @Transactional(readOnly = true)
    public Page<ArticleDTO> getPublishedArticles(Long categoryId, Long tagId, String keyword, Pageable pageable, Long currentUserId) {
        Page<Article> page = articleRepository.findPublishedArticles(categoryId, tagId, keyword, pageable);
        return page.map(article -> convertToDTO(article, currentUserId));
    }

    @Transactional
    public ArticleDetailDTO getArticleDetail(Long id, Long currentUserId) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        // 增加阅读数
        articleRepository.incrementViewCount(id);
        article.setViewCount(article.getViewCount() + 1);
        return convertToDetailDTO(article, currentUserId);
    }

    // 点赞/取消点赞
    @Transactional
    public boolean toggleLike(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = new User();
        user.setId(userId);

        if (likeRepository.existsByArticleAndUser(article, user)) {
            likeRepository.deleteByArticleIdAndUserId(articleId, userId);
            article.setLikeCount(article.getLikeCount() - 1);
            return false;
        } else {
            ArticleLike like = new ArticleLike();
            like.setArticle(article);
            like.setUser(user);
            likeRepository.save(like);
            article.setLikeCount(article.getLikeCount() + 1);
            return true;
        }
    }

    // 收藏/取消收藏
    @Transactional
    public boolean toggleFavorite(Long articleId, Long userId) {
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = new User();
        user.setId(userId);

        if (favoriteRepository.existsByArticleAndUser(article, user)) {
            favoriteRepository.deleteByArticleIdAndUserId(articleId, userId);
            article.setFavoriteCount(article.getFavoriteCount() - 1);
            return false;
        } else {
            ArticleFavorite fav = new ArticleFavorite();
            fav.setArticle(article);
            fav.setUser(user);
            favoriteRepository.save(fav);
            article.setFavoriteCount(article.getFavoriteCount() + 1);
            return true;
        }
    }

    // 辅助方法：转换为DTO
    private ArticleDTO convertToDTO(Article article, Long currentUserId) {
        ArticleDTO dto = new ArticleDTO();
        dto.setId(article.getId());
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        dto.setTitle(article.getTitle());
        dto.setSummary(article.getSummary());
        dto.setCoverImage(article.getCoverImage());
        dto.setAuthor(article.getAuthor());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setFavoriteCount(article.getFavoriteCount());
        dto.setIsTop(article.getIsTop());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setTags(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));

        if (currentUserId != null) {
            Article articleProxy = new Article();
            articleProxy.setId(article.getId());
            User userProxy = new User();
            userProxy.setId(currentUserId);
            dto.setLikedByCurrentUser(likeRepository.existsByArticleAndUser(articleProxy, userProxy));
            dto.setFavoritedByCurrentUser(favoriteRepository.existsByArticleAndUser(articleProxy, userProxy));
        }
        return dto;
    }

    private ArticleDetailDTO convertToDetailDTO(Article article, Long currentUserId) {
        ArticleDetailDTO dto = new ArticleDetailDTO();
        dto.setId(article.getId());
        dto.setCategoryId(article.getCategory() != null ? article.getCategory().getId() : null);
        dto.setCategoryName(article.getCategory() != null ? article.getCategory().getName() : null);
        dto.setTitle(article.getTitle());
        dto.setSummary(article.getSummary());
        dto.setContent(article.getContent());
        dto.setCoverImage(article.getCoverImage());
        dto.setAuthor(article.getAuthor());
        dto.setViewCount(article.getViewCount());
        dto.setLikeCount(article.getLikeCount());
        dto.setFavoriteCount(article.getFavoriteCount());
        dto.setIsTop(article.getIsTop());
        dto.setPublishedAt(article.getPublishedAt());
        dto.setTags(article.getTags().stream().map(Tag::getName).collect(Collectors.toSet()));

        if (currentUserId != null) {
            Article articleProxy = new Article();
            articleProxy.setId(article.getId());
            User userProxy = new User();
            userProxy.setId(currentUserId);
            dto.setLikedByCurrentUser(likeRepository.existsByArticleAndUser(articleProxy, userProxy));
            dto.setFavoritedByCurrentUser(favoriteRepository.existsByArticleAndUser(articleProxy, userProxy));
        }
        return dto;
    }
}