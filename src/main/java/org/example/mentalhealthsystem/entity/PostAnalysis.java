package org.example.mentalhealthsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "post_analysis")
public class PostAnalysis {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "emotion_primary", length = 50)
    private String emotionPrimary;

    @Column(name = "emotion_secondary", length = 200)
    private String emotionSecondary; // 存储 JSON 数组字符串

    @Column(name = "emotion_intensity")
    private Integer emotionIntensity;

    @Column(name = "positive_indicators", columnDefinition = "TEXT")
    private String positiveIndicators; // JSON 数组

    @Column(name = "risk_indicators", columnDefinition = "TEXT")
    private String riskIndicators; // JSON 数组

    @Column(name = "wellbeing_score")
    private Integer wellbeingScore;

    @Column(columnDefinition = "TEXT")
    private String suggestions; // JSON 数组

    @Column(name = "recommended_tools", columnDefinition = "TEXT")
    private String recommendedTools; // JSON 数组

    @Column(name = "raw_response", columnDefinition = "TEXT")
    private String rawResponse;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}