package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class SensitiveWordDTO {
    private Long id;
    private String word;
    private Integer severity;
}