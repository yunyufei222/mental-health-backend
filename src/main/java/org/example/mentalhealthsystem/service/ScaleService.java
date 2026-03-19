package org.example.mentalhealthsystem.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.mentalhealthsystem.dto.*;
import org.example.mentalhealthsystem.entity.*;
import org.example.mentalhealthsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ScaleService {

    @Autowired
    private ScaleRepository scaleRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private OptionRepository optionRepository;

    @Autowired
    private ObjectMapper objectMapper;

    // 管理员：创建量表
    @Transactional
    public Scale createScale(ScaleDTO scaleDTO) {
        Scale scale = new Scale();
        scale.setName(scaleDTO.getName());
        scale.setCode(scaleDTO.getCode());
        scale.setDescription(scaleDTO.getDescription());
        scale.setInstruction(scaleDTO.getInstruction());
        scale.setDimensionCount(scaleDTO.getDimensionCount());
        scale.setIsActive(true);
        return scaleRepository.save(scale);
    }

    // 管理员：更新量表基本信息
    @Transactional
    public Scale updateScale(Long id, ScaleDTO scaleDTO) {
        Scale scale = scaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        scale.setName(scaleDTO.getName());
        scale.setDescription(scaleDTO.getDescription());
        scale.setInstruction(scaleDTO.getInstruction());
        scale.setDimensionCount(scaleDTO.getDimensionCount());
        scale.setIsActive(scaleDTO.getIsActive());
        return scaleRepository.save(scale);
    }

    // 管理员：删除量表（逻辑删除，置为不可用）
    @Transactional
    public void deleteScale(Long id) {
        Scale scale = scaleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        scale.setIsActive(false);
        scaleRepository.save(scale);
    }

    // 管理员：添加题目到量表
    @Transactional
    public Question addQuestion(Long scaleId, QuestionDTO questionDTO) {
        Scale scale = scaleRepository.findById(scaleId)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        Question question = new Question();
        question.setScale(scale);
        question.setQuestionText(questionDTO.getQuestionText());
        question.setDimension(questionDTO.getDimension());
        question.setSortOrder(questionDTO.getSortOrder());
        question = questionRepository.save(question);

        if (questionDTO.getOptions() != null) {
            for (OptionDTO optDTO : questionDTO.getOptions()) {
                Option option = new Option();
                option.setQuestion(question);
                option.setOptionText(optDTO.getOptionText());
                option.setScore(optDTO.getScore());
                option.setSortOrder(optDTO.getSortOrder());
                optionRepository.save(option);
            }
        }
        return question;
    }

    // 管理员：更新题目
    @Transactional
    public Question updateQuestion(Long questionId, QuestionDTO questionDTO) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        question.setQuestionText(questionDTO.getQuestionText());
        question.setDimension(questionDTO.getDimension());
        question.setSortOrder(questionDTO.getSortOrder());
        return questionRepository.save(question);
    }

    // 管理员：删除题目（级联删除选项）
    @Transactional
    public void deleteQuestion(Long questionId) {
        questionRepository.deleteById(questionId);
    }

    // 管理员：添加选项
    @Transactional
    public Option addOption(Long questionId, OptionDTO optionDTO) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("题目不存在"));
        Option option = new Option();
        option.setQuestion(question);
        option.setOptionText(optionDTO.getOptionText());
        option.setScore(optionDTO.getScore());
        option.setSortOrder(optionDTO.getSortOrder());
        return optionRepository.save(option);
    }

    // 管理员：更新选项
    @Transactional
    public Option updateOption(Long optionId, OptionDTO optionDTO) {
        Option option = optionRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("选项不存在"));
        option.setOptionText(optionDTO.getOptionText());
        option.setScore(optionDTO.getScore());
        option.setSortOrder(optionDTO.getSortOrder());
        return optionRepository.save(option);
    }

    // 管理员：删除选项
    @Transactional
    public void deleteOption(Long optionId) {
        optionRepository.deleteById(optionId);
    }

    // 用户端：获取所有活跃量表列表（不含题目）
    public Page<ScaleDTO> getActiveScales(Pageable pageable) {
        Page<Scale> page = scaleRepository.findByIsActiveTrue(pageable);
        return page.map(this::convertToBasicDTO);
    }

    // 用户端：获取量表详情（含题目和选项）
    public ScaleDTO getScaleDetail(Long scaleId) {
        Scale scale = scaleRepository.findById(scaleId)
                .orElseThrow(() -> new RuntimeException("量表不存在"));
        if (!scale.getIsActive()) {
            throw new RuntimeException("量表已停用");
        }
        return convertToDetailDTO(scale);
    }

    private ScaleDTO convertToBasicDTO(Scale scale) {
        ScaleDTO dto = new ScaleDTO();
        dto.setId(scale.getId());
        dto.setName(scale.getName());
        dto.setCode(scale.getCode());
        dto.setDescription(scale.getDescription());
        dto.setInstruction(scale.getInstruction());
        dto.setDimensionCount(scale.getDimensionCount());
        dto.setQuestionCount(scale.getQuestions() != null ? scale.getQuestions().size() : 0);
        dto.setIsActive(scale.getIsActive());
        dto.setCreatedAt(scale.getCreatedAt());
        dto.setUpdatedAt(scale.getUpdatedAt());
        return dto;
    }

    private ScaleDTO convertToDetailDTO(Scale scale) {
        ScaleDTO dto = convertToBasicDTO(scale);
        List<QuestionDTO> questionDTOs = scale.getQuestions().stream()
                .map(this::convertQuestionToDTO)
                .collect(Collectors.toList());
        dto.setQuestions(questionDTOs);
        return dto;
    }

    private QuestionDTO convertQuestionToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setId(question.getId());
        dto.setQuestionText(question.getQuestionText());
        dto.setDimension(question.getDimension());
        dto.setSortOrder(question.getSortOrder());
        List<OptionDTO> optionDTOs = question.getOptions().stream()
                .map(this::convertOptionToDTO)
                .collect(Collectors.toList());
        dto.setOptions(optionDTOs);
        return dto;
    }

    private OptionDTO convertOptionToDTO(Option option) {
        OptionDTO dto = new OptionDTO();
        dto.setId(option.getId());
        dto.setOptionText(option.getOptionText());
        dto.setScore(option.getScore());
        dto.setSortOrder(option.getSortOrder());
        return dto;
    }
}