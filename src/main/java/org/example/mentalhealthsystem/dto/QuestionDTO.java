package org.example.mentalhealthsystem.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDTO {
    private Long id;
    private String questionText;
    private String dimension;
    private Integer sortOrder;
    private List<OptionDTO> options;
}