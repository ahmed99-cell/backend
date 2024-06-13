package com.bezkoder.spring.security.postgresql.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;
@Getter
@Setter
public class QuestionDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private Date createdAt;
    private Date updatedAt;

    private int views;



    private Set<String> tags;
    private int voteCount;
    private int answerCount;
}
