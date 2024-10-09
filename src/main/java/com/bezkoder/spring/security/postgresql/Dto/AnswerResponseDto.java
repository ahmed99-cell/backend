package com.bezkoder.spring.security.postgresql.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class AnswerResponseDto {


    private Long id;
    private String content;
    private String username;
    private String createdAt;
    private String updatedAt;
    private Long questionId;
    private Long answerId;
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    private List<String> votes;
    private List<String> favorites;



}
