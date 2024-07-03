package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Getter
@Setter
@Entity
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean markedAsFavorite;
    @ManyToOne
    @JsonIgnore
    private User user;
    @ManyToOne
    @JsonIgnore
    private Question question;

    @ManyToOne
    @JsonIgnore
    private Answer answer;
    @ManyToOne
    @JsonIgnore
    private AnswerResponse answerResponse;

    public boolean isMarkedAsFavorite() {
        return markedAsFavorite;
    }

    public void setMarkedAsFavorite(boolean markedAsFavorite) {
        this.markedAsFavorite = markedAsFavorite;
    }
}
