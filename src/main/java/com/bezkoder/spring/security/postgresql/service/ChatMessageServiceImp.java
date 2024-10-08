package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.ChatMessage;
import com.bezkoder.spring.security.postgresql.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class ChatMessageServiceImp implements ChatMessageService  {
    @Autowired
    private ChatMessageRepository repository;

    @Autowired
    private ChatRoomServiceImp chatRoomService;

    @Override
    public ChatMessage saveMessage(ChatMessage chatMessage) {
        String chatId = chatRoomService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getReceiverId(), true)
                .orElseThrow(); // You can replace this with a custom exception if needed
        chatMessage.setChatId(chatId);
        return repository.save(chatMessage);
    }
    @Override
    public List<ChatMessage> findChatMessages(Long senderId, Long receiverId) {
        String chatId = chatRoomService.getChatRoomId(senderId, receiverId, false)
                .orElse(null);

        return chatId != null ? repository.findByChatId(chatId) : new ArrayList<>();
    }



}