package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Favorite;

import java.util.List;

public interface FavoriteService {
    List<Favorite> getAllFavorites();
    Favorite getFavoriteById(Long id);
    Favorite saveFavorite(Favorite favorite);
    void deleteFavorite(Long id);
    Favorite markQuestionAsFavorite( Long questionId);
    Favorite markAnswerAsFavorite( Long answerId);
    Favorite markAnswerResponseAsFavorite( Long answerResponseId);


}
