package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.models.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteRepository extends JpaRepository<Vote,Long> {
    Vote findByUserAndEntityIdAndEntityType(User user, Long entityId, String entityType);

}
