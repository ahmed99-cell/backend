package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.ChatMessage;


import java.util.List;

public interface ChatMessageService {
    List<ChatMessage> findChatMessages(Long senderId, Long receiverId);
    ChatMessage saveMessage(ChatMessage chatMessage);
}