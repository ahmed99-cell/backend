package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer,Long> {
    @Query("SELECT a FROM Answer a WHERE a.user.matricule = :userId AND a.createdAt BETWEEN :startDate AND :endDate")
    List<Answer> findByUser_MatriculeAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
    List<Answer> findByQuestionId(Long questionId);

    @Query("SELECT COUNT(1) FROM Answer a WHERE a.user.matricule = :userId")
    int countAnswersByUserId(@Param("userId") Long userId);
}
