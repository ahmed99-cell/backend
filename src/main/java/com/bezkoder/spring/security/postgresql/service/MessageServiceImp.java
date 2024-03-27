package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Message;
import com.bezkoder.spring.security.postgresql.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service

public class MessageServiceImp implements MessageService{
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    public MessageServiceImp(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }
    @Override
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }
}
