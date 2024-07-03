package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Getter
@Setter
@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Lob
    private byte[] file;

    public Boolean getUserAnonymous() {
        return isUserAnonymous;
    }

    public void setUserAnonymous(Boolean userAnonymous) {
        isUserAnonymous = userAnonymous;
    }

    private String contentType;
    private Boolean isUserAnonymous = false;
    private int views;


    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;



    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "question_tags",
            joinColumns = @JoinColumn(name = "question_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();


    @OneToMany(mappedBy = "question")
    private List<Favorite> favorites;


    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Vote> votes = new HashSet<>();


    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Answer> answers = new HashSet<>();
}
