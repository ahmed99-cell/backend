package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")

    @JsonIgnore

    private User user;
    @OneToMany(mappedBy = "parentAnswer", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.EAGER)
    private Set<AnswerResponse> responses = new HashSet<>();
    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;


    @Lob
    private byte[] file;






    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    @JsonIgnore
    private Question question;
    @Column(columnDefinition = "TEXT")
    private String content;


    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true,fetch = FetchType.EAGER)
    private Set<Vote> votes = new HashSet<>();

    @OneToMany(mappedBy = "answer",fetch = FetchType.EAGER)
    private List<Favorite> favorites;
}
