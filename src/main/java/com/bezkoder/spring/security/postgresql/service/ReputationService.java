package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Reputation;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.ReputationRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service

public class ReputationService {
    private final ReputationRepository reputationRepository;
    private final UserRepository userRepository;


    @Autowired
    public ReputationService(ReputationRepository reputationRepository, UserRepository userRepository) {
        this.reputationRepository = reputationRepository;
        this.userRepository = userRepository;
    }
    public List<Reputation> getAllReputations() {
        return reputationRepository.findAll();
    }

    public Reputation getReputationById(Long id) {
        return reputationRepository.findById(id).orElse(null);
    }

    public Reputation createReputation(Reputation reputation) {
        User user = userRepository.findById(reputation.getUser().getMatricule()).orElse(null);
        if (user != null) {
            reputation.setUser(user);
            return reputationRepository.save(reputation);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public Reputation updateReputation(Reputation reputation) {
        return reputationRepository.save(reputation);
    }

    public void deleteReputation(Long id) {
        reputationRepository.deleteById(id);
    }

}
