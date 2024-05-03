package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.service.VoteService;
import com.bezkoder.spring.security.postgresql.service.VoteServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
public class VoteController {
    @Autowired
    private VoteService voteService;

    @PostMapping("/{entityType}/{entityId}")
    public ResponseEntity<String> vote(@PathVariable Long entityId, @PathVariable String entityType, @RequestBody int value, @RequestParam Long userId) {
        voteService.vote(userId, entityId, entityType, value);
        return ResponseEntity.ok("Voted successfully");
    }
}
