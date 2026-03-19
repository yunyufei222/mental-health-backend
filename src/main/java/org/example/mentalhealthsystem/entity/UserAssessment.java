package org.example.mentalhealthsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_assessment")
public class UserAssessment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "scale_id", nullable = false)
    private Scale scale;

    @Column(name = "total_score", nullable = false)
    private Integer totalScore;

    @Column(columnDefinition = "JSON")
    private String dimensionScores; // 存储各维度得分，如 {"P":12, "E":10, ...}

    @Column(columnDefinition = "JSON")
    private String answers; // 存储用户答案，如 [1,2,3,2,...] 对应选项ID或分值

    @Column(columnDefinition = "TEXT")
    private String interpretation; // 结果解读文本

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}