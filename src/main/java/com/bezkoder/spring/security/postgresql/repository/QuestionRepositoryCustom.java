package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Question;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface QuestionRepositoryCustom {
    List<Question> findByCriteria(String title, String content, Long matricule, List<String> tags, Pageable pageable);
}
