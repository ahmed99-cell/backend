package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
@Repository

public class QuestionRepositoryImpl implements QuestionRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public List<Question> findByCriteria(String title, String content, Long matricule, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> cq = cb.createQuery(Question.class);

        Root<Question> question = cq.from(Question.class);
        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(cb.like(question.get("title"), "%" + title + "%"));
        }
        if (content != null) {
            predicates.add(cb.like(question.get("content"), "%" + content + "%"));
        }
        if (matricule != null) {
            predicates.add(cb.equal(question.get("user").get("matricule"), matricule));
        }

        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Question> query = entityManager.createQuery(cq);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }
}