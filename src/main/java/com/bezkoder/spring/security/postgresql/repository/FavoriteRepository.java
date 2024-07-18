package com.bezkoder.spring.security.postgresql.repository;

import com.bezkoder.spring.security.postgresql.models.Favorite;
import com.bezkoder.spring.security.postgresql.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Long>{
    List<Favorite> findByUserAndMarkedAsFavorite(User user, boolean markedAsFavorite);
    void deleteByQuestionId(Long questionId);

}
