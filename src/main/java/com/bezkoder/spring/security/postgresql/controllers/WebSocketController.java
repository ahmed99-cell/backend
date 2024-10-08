

package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.ChatMessage;
import com.bezkoder.spring.security.postgresql.models.ChatNotifications;
import com.bezkoder.spring.security.postgresql.service.ChatMessageServiceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageServiceImp messageService;

    @MessageMapping("/chat")
    public void processMessageFromClient(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        chatMessage.setTimestamp(LocalDateTime.now());
        System.out.println("Message received: " + chatMessage.getContent() + " from " + chatMessage.getSenderId() + " to " + chatMessage.getReceiverId());


        ChatMessage savedMessage = messageService.saveMessage(chatMessage);


        messagingTemplate.convertAndSendToUser(
                savedMessage.getReceiverId().toString(),
                "/queue/messages",
                new ChatNotifications(
                        savedMessage.getId(),
                        savedMessage.getSenderId(),
                        savedMessage.getReceiverId(),
                        savedMessage.getContent()
                )
        );

        System.out.println("Message sent to user: " + chatMessage.getReceiverId());
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<ChatMessage>> findChatMessages(
            @PathVariable Long senderId,
            @PathVariable Long recipientId) {
        return ResponseEntity.ok(messageService.findChatMessages(senderId, recipientId));
    }
}