package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.payload.request.AnswerRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AnswerRequestWrapper {
    @Valid
    private AnswerRequest answerRequest;

    private MultipartFile file;






    public AnswerRequest getAnswerRequest() {
        return answerRequest;
    }

    public void setAnswerRequest(AnswerRequest answerRequest) {
        this.answerRequest = answerRequest;
    }

    public MultipartFile getFile() {
        return file;
    }

    public void setFile(MultipartFile file) {
        this.file = file;
    }
}
