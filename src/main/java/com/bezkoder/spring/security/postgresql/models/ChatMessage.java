package com.bezkoder.spring.security.postgresql.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chat_messages")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender ;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private User receiver ;
    @Column(nullable = false)
    private String content;
    private MessageType type ;
    @Column(nullable = false)
    private LocalDateTime timestamp;

    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }


}
