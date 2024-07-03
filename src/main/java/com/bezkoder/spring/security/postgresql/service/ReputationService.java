package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Reputation;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.AnswerRepository;
import com.bezkoder.spring.security.postgresql.repository.QuestionRepository;
import com.bezkoder.spring.security.postgresql.repository.ReputationRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReputationService {
    @Autowired
    private ReputationRepository reputationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    public List<Reputation> getAllReputations() {
        return reputationRepository.findAll();
    }

    public Reputation getReputationById(Long id) {
        return reputationRepository.findById(id).orElse(null);
    }

    public Reputation createReputation(Reputation reputation) {
        User user = userRepository.findById(reputation.getUser().getMatricule()).orElse(null);
        if (user != null) {
            int score = calculateScoreForUser(user);
            reputation.setUser(user);
            reputation.setScore(score);
            return reputationRepository.save(reputation);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public int calculateScoreForUser(User user) {
        int questionsCount = questionRepository.countQuestionsByUserId(user.getMatricule());
        int answersCount = answerRepository.countAnswersByUserId(user.getMatricule());

        // Define your scoring logic here
        int score = (questionsCount * 2) + (answersCount * 3); // Example scoring: 2 points per question, 3 points per answer
        return score;
    }

    public Reputation updateReputation(Reputation reputation) {
        return reputationRepository.save(reputation);
    }

    public void deleteReputation(Long id) {
        reputationRepository.deleteById(id);
    }
}
