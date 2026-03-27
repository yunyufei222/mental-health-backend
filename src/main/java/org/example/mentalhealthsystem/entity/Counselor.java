package org.example.mentalhealthsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "counselor")
public class Counselor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String qualification;      // 资质证书
    private String expertise;          // 擅长领域
    @Column(columnDefinition = "TEXT")
    private String introduction;       // 个人简介
    private BigDecimal price;          // 咨询费用

    private BigDecimal rating = BigDecimal.ZERO;
    @Column(name = "review_count")
    private Integer reviewCount = 0;

    private Integer status = 1;        // 1-正常，0-停用

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}