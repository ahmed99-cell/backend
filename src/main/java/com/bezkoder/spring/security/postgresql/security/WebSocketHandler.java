package com.bezkoder.spring.security.postgresql.security;

import com.bezkoder.spring.security.postgresql.models.Message;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.MessageRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        MessagePayload payload = objectMapper.readValue(message.getPayload(), MessagePayload.class);

        // Vérifier si l'utilisateur tente d'envoyer un message à lui-même
        if (payload.getSenderId() == payload.getReceiverId()) {
            System.err.println("Un utilisateur ne peut pas envoyer un message à lui-même");
            return; // ou gérer l'erreur de manière appropriée
        }

        // Récupérer l'expéditeur et le destinataire depuis la base de données
        User sender = userRepository.findById(payload.getSenderId()).orElse(null);
        User receiver = userRepository.findById(payload.getReceiverId()).orElse(null);

        if (sender == null || receiver == null) {
            System.err.println("Sender ou Receiver introuvable");
            return;
        }

        Message msg = new Message();
        msg.setContent(payload.getContent());
        msg.setSender(sender);
        msg.setReceiver(receiver);
        messageRepository.save(msg);
    }
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
    }
}
