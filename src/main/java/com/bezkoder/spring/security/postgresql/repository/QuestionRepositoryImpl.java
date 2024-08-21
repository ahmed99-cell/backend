package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Question;
import com.bezkoder.spring.security.postgresql.models.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Repository

public class QuestionRepositoryImpl implements QuestionRepositoryCustom{
    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    public List<Question> findByCriteria(String title, String content, Long matricule, List<String> tags, Boolean isUserAnonymous, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Question> cq = cb.createQuery(Question.class);

        Root<Question> question = cq.from(Question.class);
        List<Predicate> predicates = new ArrayList<>();

        if (title != null) {
            predicates.add(cb.like(cb.lower(question.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (content != null) {
            predicates.add(cb.like(cb.lower(question.get("content")), "%" + content.toLowerCase() + "%"));
        }
        if (tags != null && !tags.isEmpty()) {
            Join<Question, Tag> tagsJoin = question.join("tags", JoinType.INNER);
            Predicate tagPredicate = cb.or(tags.stream()
                    .map(tag -> cb.equal(tagsJoin.get("name"), tag))
                    .toArray(Predicate[]::new));
            predicates.add(tagPredicate);
        }
        if (matricule != null) {
            predicates.add(cb.equal(question.get("user").get("matricule"), matricule));
        }
        if (isUserAnonymous != null) {
            predicates.add(cb.equal(question.get("isUserAnonymous"), isUserAnonymous));
        }
        cq.orderBy(cb.desc(question.get("createdAt")), cb.desc(question.get("updatedAt")));
        cq.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Question> query = entityManager.createQuery(cq);
        query.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        query.setMaxResults(pageable.getPageSize());

        return query.getResultList();
    }

}