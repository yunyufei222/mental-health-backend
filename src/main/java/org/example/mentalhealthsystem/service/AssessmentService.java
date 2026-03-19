package org.example.mentalhealthsystem.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mentalhealthsystem.dto.AssessmentResultDTO;
import org.example.mentalhealthsystem.dto.AssessmentSubmitRequest;
import org.example.mentalhealthsystem.dto.UserAssessmentDTO;
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
public class AssessmentService {

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private UserAssessmentRepository assessmentRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public AssessmentResultDTO submitAssessment(Long userId, AssessmentSubmitRequest request) {
        Scale scale = scaleRepository.findById(request.getScaleId())
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        if (!scale.getIsActive()) {
            throw new RuntimeException("量表已停用");
        }

        // 获取所有题目并按顺序排序
        List<Question> questions = questionRepository.findByScaleIdOrderBySortOrder(scale.getId());
        if (questions.size() != request.getAnswers().size()) {
            throw new RuntimeException("答案数量与题目数量不匹配");
        }

        // 计算总分和各维度得分
        int totalScore = 0;
        Map<String, Integer> dimensionScores = new HashMap<>();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            int answerScore = request.getAnswers().get(i);
            totalScore += answerScore;

            String dim = q.getDimension();
            if (dim != null && !dim.isEmpty()) {
                dimensionScores.put(dim, dimensionScores.getOrDefault(dim, 0) + answerScore);
            }
        }

        // 生成解读（此处简化，实际可根据分数范围生成）
        String interpretation = generateInterpretation(scale, totalScore, dimensionScores);

        // 保存测评记录
        UserAssessment assessment = new UserAssessment();
        assessment.setUser(new User() {{ setId(userId); }});
        assessment.setScale(scale);
        assessment.setTotalScore(totalScore);
        try {
            assessment.setDimensionScores(objectMapper.writeValueAsString(dimensionScores));
            assessment.setAnswers(objectMapper.writeValueAsString(request.getAnswers()));
        } catch (Exception e) {
            throw new RuntimeException("数据转换失败", e);
        }
        assessment.setInterpretation(interpretation);
        assessment = assessmentRepository.save(assessment);

        // 构造返回结果
        AssessmentResultDTO result = new AssessmentResultDTO();
        result.setId(assessment.getId());
        result.setScaleId(scale.getId());
        result.setScaleName(scale.getName());
        result.setTotalScore(totalScore);
        result.setDimensionScores(dimensionScores);
        result.setInterpretation(interpretation);
        result.setCreatedAt(assessment.getCreatedAt().toString());
        return result;
    }

    // 获取用户测评历史
    public Page<UserAssessmentDTO> getUserAssessmentHistory(Long userId, Pageable pageable) {
        Page<UserAssessment> page = assessmentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return page.map(this::convertToUserDTO);
    }

    private UserAssessmentDTO convertToUserDTO(UserAssessment assessment) {
        UserAssessmentDTO dto = new UserAssessmentDTO();
        dto.setId(assessment.getId());
        dto.setScaleId(assessment.getScale().getId());
        dto.setScaleName(assessment.getScale().getName());
        dto.setTotalScore(assessment.getTotalScore());
        dto.setCreatedAt(assessment.getCreatedAt());
        return dto;
    }

    private String generateInterpretation(Scale scale, int totalScore, Map<String, Integer> dimScores) {
        // 这里可以接入规则引擎或固定文本
        // 简单返回一个示例
        return "您的总分为 " + totalScore + "。感谢参与测评。";
    }
}