package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import org.springframework.web.multipart.MultipartFile;

public class QuestionRequestWrapper {
    private QuestionRequest questionRequest;
    private MultipartFile file;
    private Long tagId;

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

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