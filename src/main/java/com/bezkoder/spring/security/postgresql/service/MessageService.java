package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.Message;

public interface MessageService {
     Message saveMessage(Message message);
}
