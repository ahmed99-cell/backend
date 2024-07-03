package com.bezkoder.spring.security.postgresql.Dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.Set;
@Getter
@Setter
public class QuestionByIdDto {
    private Long Id;
    private byte[] file;
    private String contentType;
    private List<AnswerDto> answers;
    private List<FavoriteDto> favorites;
    private String content;
    private String username;
    private Date createdAt;
    private Date updatedAt;
    private Set<String> tags;
    private String Title;


}
