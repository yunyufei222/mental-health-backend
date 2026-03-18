package org.example.mentalhealthsystem.service;

import org.example.mentalhealthsystem.dto.TagDTO;
import org.example.mentalhealthsystem.entity.Tag;
import org.example.mentalhealthsystem.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    public Page<TagDTO> getAllTags(Pageable pageable) {
        return tagRepository.findAll(pageable).map(this::convertToDTO);
    }

    @Transactional
    public TagDTO createTag(String name) {
        if (tagRepository.existsByName(name)) {
            throw new RuntimeException("标签已存在");
        }
        Tag tag = new Tag();
        tag.setName(name);
        tag = tagRepository.save(tag);
        return convertToDTO(tag);
    }

    @Transactional
    public void deleteTag(Long id) {
        tagRepository.deleteById(id);
    }

    private TagDTO convertToDTO(Tag tag) {
        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        return dto;
    }
}