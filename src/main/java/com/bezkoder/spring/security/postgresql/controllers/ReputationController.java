package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Reputation;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.security.services.UserDetailsImpl;
import com.bezkoder.spring.security.postgresql.service.ReputationService;
import com.bezkoder.spring.security.postgresql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/reputations")
public class ReputationController {
    private final ReputationService reputationService;
    private final UserRepository userRepository;
    private final UserService userService;

    @Autowired
    public ReputationController(ReputationService reputationService, UserRepository userRepository, UserService userService) {
        this.reputationService = reputationService;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    @GetMapping("/getAll")
    public List<Reputation> getAllReputations() {
        return reputationService.getAllReputations();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reputation> getReputationById(@PathVariable Long id) {
        Reputation reputation = reputationService.getReputationById(id);
        if (reputation != null) {
            return ResponseEntity.ok(reputation);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public Reputation createReputation(@AuthenticationPrincipal UserDetailsImpl currentUser, @RequestBody Reputation reputation) {
        User user = userService.getUserById(currentUser.getMatricule());
        if (user != null) {
            int score = reputationService.calculateScoreForUser(user); // Calculate score based on user's activity
            reputation.setUser(user);
            reputation.setScore(score);
            return reputationService.createReputation(reputation);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reputation> updateReputation(@PathVariable Long id, @RequestBody Reputation reputation) {
        Reputation existingReputation = reputationService.getReputationById(id);
        if (existingReputation != null) {
            reputation.setId(id);
            return ResponseEntity.ok(reputationService.updateReputation(reputation));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReputation(@PathVariable Long id) {
        Reputation existingReputation = reputationService.getReputationById(id);
        if (existingReputation != null) {
            reputationService.deleteReputation(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
