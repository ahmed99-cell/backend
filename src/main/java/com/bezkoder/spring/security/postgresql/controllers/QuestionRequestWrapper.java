package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Tag;
import com.bezkoder.spring.security.postgresql.payload.request.QuestionRequest;
import org.springframework.web.multipart.MultipartFile;

public class QuestionRequestWrapper {
    private QuestionRequest questionRequest;
    private MultipartFile file;
    private Tag tag;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
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
