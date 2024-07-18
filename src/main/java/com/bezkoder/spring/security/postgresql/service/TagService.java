package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.models.Question;
import com.bezkoder.spring.security.postgresql.models.Tag;
import com.bezkoder.spring.security.postgresql.repository.QuestionRepository;
import com.bezkoder.spring.security.postgresql.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class TagService {
    @Autowired
    TagRepository tagRepository;
    @Autowired
    QuestionRepository questionRepository;
    public Tag createTag(Tag tag) {
        return tagRepository.save(tag);
    }

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }


    public Optional<Tag> getTagById(Long id) {
        return tagRepository.findById(id);
    }


    public Tag updateTag(Long id, Tag tagDetails) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag", "id", id));

        tag.setName(tagDetails.getName());
        tag.setDescription(tagDetails.getDescription());

        return tagRepository.save(tag);
    }


    public void deleteTag(Long tagId) {
        Tag tag = tagRepository.findById(tagId).orElseThrow(() -> new ResourceNotFoundException("Tag", "id", tagId));

        Set<Question> questions = tag.getQuestions();
        for (Question question : questions) {
            question.getTags().remove(tag);
            questionRepository.save(question);
        }

        tagRepository.delete(tag);
    }
}
