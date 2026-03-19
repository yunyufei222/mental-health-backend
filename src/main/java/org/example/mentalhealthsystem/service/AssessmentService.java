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

@Service
public class AssessmentService {

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private QuestionRepository questionRepository;

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

        List<Question> questions = questionRepository.findByScaleIdOrderBySortOrder(scale.getId());
        if (questions.size() != request.getAnswers().size()) {
            throw new RuntimeException("答案数量与题目数量不匹配");
        }

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

        String interpretation = generateInterpretation(scale, totalScore, dimensionScores);

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

        AssessmentResultDTO result = new AssessmentResultDTO();
        result.setId(assessment.getId());
        result.setScaleId(scale.getId());
        result.setScaleName(scale.getName());
        result.setTotalScore(totalScore);
        result.setDimensionScores(dimensionScores);
        result.setInterpretation(interpretation);
        result.setDetailedInterpretation(interpretation);
        result.setCreatedAt(assessment.getCreatedAt().toString());
        return result;
    }

    public Page<UserAssessmentDTO> getUserAssessmentHistory(Long userId, Pageable pageable) {
        Page<UserAssessment> page = assessmentRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
        return page.map(this::convertToUserDTO);
    }

    @Transactional(readOnly = true)
    public AssessmentResultDTO getAssessmentResultById(Long assessmentId, Long userId) {
        UserAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("测评记录不存在"));
        if (!assessment.getUser().getId().equals(userId)) {
            throw new RuntimeException("无权查看此记录");
        }
        return convertToResultDTO(assessment);
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

    private AssessmentResultDTO convertToResultDTO(UserAssessment assessment) {
        AssessmentResultDTO dto = new AssessmentResultDTO();
        dto.setId(assessment.getId());
        dto.setScaleId(assessment.getScale().getId());
        dto.setScaleName(assessment.getScale().getName());
        dto.setTotalScore(assessment.getTotalScore());
        dto.setInterpretation(assessment.getInterpretation());
        dto.setDetailedInterpretation(assessment.getInterpretation());
        dto.setCreatedAt(assessment.getCreatedAt().toString());
        try {
            if (assessment.getDimensionScores() != null) {
                Map<String, Integer> dimScores = objectMapper.readValue(assessment.getDimensionScores(), new TypeReference<Map<String, Integer>>() {});
                dto.setDimensionScores(dimScores);
            }
        } catch (Exception e) {
            // ignore
        }
        return dto;
    }

    private String generateInterpretation(Scale scale, int totalScore, Map<String, Integer> dimScores) {
        if (scale.getId() == 1) { // 繁荣量表
            if (totalScore <= 24) return "您的繁荣感较低，可能需要关注心理状态，尝试积极活动。";
            else if (totalScore <= 40) return "您的繁荣感中等，可以继续保持，多参与有意义的事情。";
            else return "您的繁荣感很高，生活充实，请保持！";
        } else if (scale.getId() == 2) { // VIA量表
            StringBuilder sb = new StringBuilder("您的品格优势得分：\n");
            dimScores.forEach((dim, score) -> sb.append(dim).append(": ").append(score).append("\n"));
            return sb.toString();
        }
        return "您的总分为 " + totalScore + "。感谢参与测评。";
    }
}