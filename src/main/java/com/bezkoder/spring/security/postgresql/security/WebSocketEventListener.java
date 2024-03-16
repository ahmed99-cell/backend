package com.bezkoder.spring.security.postgresql.security;

import com.bezkoder.spring.security.postgresql.models.ChatMessage;
import com.bezkoder.spring.security.postgresql.models.MessageType;
import com.bezkoder.spring.security.postgresql.models.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {
    private final SimpMessageSendingOperations messageTemplate;
    @EventListener
    public void handeleWebSocketDisconnectListener(SessionDisconnectEvent event){
        StompHeaderAccessor headerAccessor =StompHeaderAccessor .wrap(event.getMessage());
        User user =  new User();
         String username =user.getUsername();
         username = (String) headerAccessor.getSessionAttributes().get("username");
         if(username != null){
             log.info("user disconnected{}",username);
             var chatMessage = ChatMessage.builder()
                     .type(MessageType.LEAVER)
                     .sender(user)
                     .build();
             messageTemplate.convertAndSend("/topic/public",chatMessage);
         }

    }
}
