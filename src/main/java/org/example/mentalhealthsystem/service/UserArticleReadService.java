package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.ArticleReadRecordDTO;
import org.example.mentalhealthsystem.entity.Article;
import org.example.mentalhealthsystem.entity.User;
import org.example.mentalhealthsystem.entity.UserArticleRead;
import org.example.mentalhealthsystem.repository.ArticleRepository;
import org.example.mentalhealthsystem.repository.UserArticleReadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserArticleReadService {

    @Autowired
    private UserArticleReadRepository readRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Transactional
    public void recordRead(Long userId, Long articleId) {
        UserArticleRead read = readRepository.findByUserIdAndArticleId(userId, articleId)
                .orElse(new UserArticleRead());
        if (read.getId() == null) {
            User user = new User();
            user.setId(userId);
            Article article = articleRepository.getReferenceById(articleId);
            read.setUser(user);
            read.setArticle(article);
        }
        readRepository.save(read); // 如果已存在，更新时间会自动更新（取决于数据库配置）
    }

    @Transactional(readOnly = true)
    public Page<ArticleReadRecordDTO> getUserReadHistory(Long userId, Pageable pageable) {
        Page<UserArticleRead> page = readRepository.findByUserIdOrderByReadAtDesc(userId, pageable);
        return page.map(read -> {
            ArticleReadRecordDTO dto = new ArticleReadRecordDTO();
            dto.setId(read.getId());
            dto.setArticleId(read.getArticle().getId());
            dto.setArticleTitle(read.getArticle().getTitle());
            dto.setArticleSummary(read.getArticle().getSummary());
            dto.setArticleCover(read.getArticle().getCoverImage());
            dto.setReadAt(read.getReadAt());
            return dto;
        });
    }
}