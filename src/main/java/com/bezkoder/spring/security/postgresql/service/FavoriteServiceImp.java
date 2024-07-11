package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class FavoriteServiceImp implements FavoriteService{
    @Autowired

    private  FavoriteRepository favoriteRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private AnswerResponseRepository answerResponseRepository;






    @Override
    public List<Favorite> getAllFavorites() {
        return favoriteRepository.findAll();

    }

    @Override
    public Favorite getFavoriteById(Long id) {
        return favoriteRepository.findById(id).orElse(null);
    }

    @Override
    public Favorite saveFavorite(Favorite favorite) {
        return favoriteRepository.save(favorite);
    }

    @Override
    public void deleteFavorite(Long id) {
        favoriteRepository.deleteById(id);

    }


    @Override
    public Favorite markQuestionAsFavorite(Long questionId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Question question = questionRepository.findById(questionId).orElseThrow(() -> new RuntimeException("Question non trouvée"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setQuestion(question);

            return favoriteRepository.save(favorite);
        } else {
            throw new RuntimeException("Utilisateur non connecté");
        }
    }

    @Override
    public Favorite markAnswerAsFavorite(Long answerId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            Answer answer = answerRepository.findById(answerId).orElseThrow(() -> new RuntimeException("Answer non trouvée"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setAnswer(answer);

            return favoriteRepository.save(favorite);
        } else {
            throw new RuntimeException("Utilisateur non connecté");
        }
    }

    @Override
    public Favorite markAnswerResponseAsFavorite(Long answerResponseId) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails)principal).getUsername();
            User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

            AnswerResponse answerResponse = answerResponseRepository.findById(answerResponseId).orElseThrow(() -> new RuntimeException("AnswerResponse non trouvée"));

            Favorite favorite = new Favorite();
            favorite.setUser(user);
            favorite.setAnswerResponse(answerResponse);

            return favoriteRepository.save(favorite);
        } else {
            throw new RuntimeException("Utilisateur non connecté");
        }
    }
    public Favorite toggleFavorite(Long id) {
        Favorite favorite = favoriteRepository.findById(id).orElseThrow(() -> new RuntimeException("Favorite not found"));
        favorite.setMarkedAsFavorite(!favorite.isMarkedAsFavorite());
        return favoriteRepository.save(favorite);
    }

    @Override
    @Transactional
    public List<Question> getFavoritesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));

        List<Favorite> nonFavoriteFavorites = favoriteRepository.findByUserAndMarkedAsFavorite(user, false);

        return nonFavoriteFavorites.stream()
                .map(Favorite::getQuestion)
                .collect(Collectors.toList());
    }

}
