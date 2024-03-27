package com.bezkoder.spring.security.postgresql.security;

import com.bezkoder.spring.security.postgresql.models.Message;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.MessageRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
@Service

public class WebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private SimpMessagingTemplate template;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    UserRepository userRepository;

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Message msg = new Message();
        msg.setContent(message.getPayload());
        long senderId= msg.getSender().getMatricule();
        long receiverId = msg.getReceiver().getMatricule();


        User sender = userRepository.findById(senderId).orElse(null);
        User receiver = userRepository.findById(receiverId).orElse(null);
        msg.setSender(sender);
        msg.setReceiver(receiver);
        messageRepository.save(msg);
    }
}
