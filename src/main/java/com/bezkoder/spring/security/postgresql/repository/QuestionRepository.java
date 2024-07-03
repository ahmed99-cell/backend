package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Date;
import java.util.List;

public interface QuestionRepository extends JpaRepository<Question,Long> {
    @Query("SELECT q FROM Question q WHERE q.user.matricule = :userId AND q.createdAt BETWEEN :startDate AND :endDate")
    List<Question> findByUser_MatriculeAndCreatedAtBetween(
            @Param("userId") Long userId,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate
    );
    List<Question> findByTagsName(String tagName);

    @Query("SELECT q FROM Question q JOIN q.user u JOIN q.tags t " +
            "WHERE q.title LIKE %:keyword% " +
            "OR u.username LIKE %:keyword% " +
            "OR t.name LIKE %:keyword%")
    List<Question> searchQuestions(@Param("keyword") String keyword);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.user.matricule = :userId")
    int countQuestionsByUserId(@Param("userId") Long userId);
}
