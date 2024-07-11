package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class QuestionRequestWrapper {
    private QuestionRequest questionRequest;
    private MultipartFile file;
    private List<Long> tagIds;

    public List<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(List<Long> tagIds) {
        this.tagIds = tagIds;
    }
    public Boolean getUserAnonymous() {
        return isUserAnonymous;
    }

    public void setUserAnonymous(Boolean userAnonymous) {
        isUserAnonymous = userAnonymous;
    }

    private Boolean isUserAnonymous;



    public QuestionRequest getQuestionRequest() {
        return questionRequest;
    }

    public void setQuestionRequest(QuestionRequest questionRequest) {
        this.questionRequest = questionRequest;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}