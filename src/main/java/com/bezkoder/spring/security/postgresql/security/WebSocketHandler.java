package com.bezkoder.spring.security.postgresql.security;

import com.bezkoder.spring.security.postgresql.models.Message;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.MessageRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
        // Parse the incoming message payload to extract sender ID, receiver ID, and content
        ObjectMapper objectMapper = new ObjectMapper();
        MessagePayload payload = objectMapper.readValue(message.getPayload(), MessagePayload.class);

        // Retrieve sender and receiver from the repository
        User sender = userRepository.findById(payload.getSenderId()).orElse(null);
        User receiver = userRepository.findById(payload.getReceiverId()).orElse(null);

        if (sender == null || receiver == null) {
            System.err.println("Sender or Receiver not found");
            return; // Exit if either sender or receiver is not found
        }

        // Create a new Message entity
        Message msg = new Message();
        msg.setContent(payload.getContent());
        msg.setSender(sender);
        msg.setReceiver(receiver);

        // Save the message to the repository
        messageRepository.save(msg);
    }

    // Payload class to map incoming messages
    private static class MessagePayload {
        private long senderId;
        private long receiverId;
        private String content;

        public long getSenderId() {
            return senderId;
        }

        public void setSenderId(long senderId) {
            this.senderId = senderId;
        }

        public long getReceiverId() {
            return receiverId;
        }

        public void setReceiverId(long receiverId) {
            this.receiverId = receiverId;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }}
