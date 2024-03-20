package com.bezkoder.spring.security.postgresql.payload.request;

import javax.validation.constraints.NotBlank;

public class AnswerRequest {
    @NotBlank
    private String content;

    // Getter and setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
