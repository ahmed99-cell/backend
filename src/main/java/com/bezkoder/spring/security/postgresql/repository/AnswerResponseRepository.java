package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.AnswerResponse;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerResponseRepository extends JpaRepository<AnswerResponse,Long> {
}
