package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.AnswerResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

public interface AnswerResponseRepository extends JpaRepository<AnswerResponse,Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM answer_responses WHERE id = :responseId", nativeQuery = true)
    void deleteByResponseId(@Param("responseId") Long responseId);

    @Query("SELECT ar FROM AnswerResponse ar WHERE ar.user.matricule = :userId AND ar.createdAt BETWEEN :startDate AND :endDate")
    List<AnswerResponse> findByUserIdAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}

