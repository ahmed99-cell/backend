package com.bezkoder.spring.security.postgresql.service;

import java.util.Optional;

public interface ChatRoomService {
    Optional<String> getChatRoomId(Long senderId, Long recipientId, boolean createNewRoomIfNotExists);
    String createChatId(Long senderId, Long recipientId);
}