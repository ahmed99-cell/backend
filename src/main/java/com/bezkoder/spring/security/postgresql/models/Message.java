package com.bezkoder.spring.security.postgresql.models;

import javax.persistence.*;

@Entity
@Table( name = "messages")
public class Message {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    @ManyToOne
    @JoinColumn(name = "sender_id")
private User sender;
    @ManyToOne
    @JoinColumn(name = "receiver_id")
private User receiver;












}
