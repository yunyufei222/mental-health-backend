package org.example.mentalhealthsystem.dto;

import lombok.Data;

@Data
public class OptionDTO {
    private Long id;
    private String optionText;
    private Integer score;
    private Integer sortOrder;
}