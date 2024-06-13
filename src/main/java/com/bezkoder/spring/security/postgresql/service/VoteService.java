package com.bezkoder.spring.security.postgresql.service;

import org.springframework.http.ResponseEntity;

public interface VoteService {
    ResponseEntity<String> vote(Long userId, Long entityId, String entityType, int value);
    public int getVoteValue(Long userId, Long entityId, String entityType);
}
