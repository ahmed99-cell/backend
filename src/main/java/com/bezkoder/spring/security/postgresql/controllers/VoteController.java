package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.repository.VoteRepository;
import com.bezkoder.spring.security.postgresql.service.VoteService;
import com.bezkoder.spring.security.postgresql.service.VoteServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalInt;

@RestController
    @RequestMapping("/api/votes")
public class VoteController {
    @Autowired
    private VoteService voteService;

@Autowired
    private VoteRepository voteRepository;


    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<String> vote(
            @PathVariable Long entityId,
            @PathVariable String entityType,
            @RequestParam Long userId,
            @RequestParam int value) {

     voteService.vote(userId, entityId, entityType, value);
        return ResponseEntity.ok("Voted successfully");


    }


    @GetMapping("/status")
    public ResponseEntity<Map<String, Integer>> getVoteStatus(
            @RequestParam Long entityId,
            @RequestParam String entityType,
            @RequestParam Long userId) {
        int voteValue = voteService.getVoteValue(userId, entityId, entityType);
        OptionalInt totalVotesOptional = OptionalInt.of(voteRepository.sumValuesByEntityId(entityId));
        int totalVotes = totalVotesOptional.orElse(0);
        Map<String, Integer> response = new HashMap<>();
        response.put("value", voteValue);
        response.put("totalVotes", totalVotes); // Convertir long en int pour mettre dans le Map
        return ResponseEntity.ok(response);
    }
}
