package com.bezkoder.spring.security.postgresql.Dto;

import com.bezkoder.spring.security.postgresql.models.Question;

import java.util.List;

public class AnswerDto {
    private Long id;
    private String content;
    private String username;

    public List<AnswerResponseDto> getResponses() {
        return responses;
    }

    public void setResponses(List<AnswerResponseDto> responses) {
        this.responses = responses;
    }

    private List<AnswerResponseDto> responses;
    private List<String> votes;
    private byte[] file;


    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    private boolean isAccepted;

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    private String contentType;

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }



    public List<String> getVotes() {
        return votes;
    }





    public void setVotes(List<String> votes) {
        this.votes = votes;
    }

    public List<String> getFavorites() {
        return favorites;
    }

    public void setFavorites(List<String> favorites) {
        this.favorites = favorites;
    }

    private List<String> favorites;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    private String createdAt;
    private String updatedAt;
    private Long questionId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    private Long userId;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
