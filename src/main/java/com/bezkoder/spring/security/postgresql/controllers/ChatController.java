package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.ChatMessage;
import com.bezkoder.spring.security.postgresql.models.User;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
  public ChatMessage sendMessage ( @Payload ChatMessage chatMessage){
        return chatMessage;

  }
  public ChatMessage addUser (@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor){
      String username = chatMessage.getSender().getUsername();
        headerAccessor.getSessionAttributes().put("username",username);
        return  chatMessage;
  }

}
