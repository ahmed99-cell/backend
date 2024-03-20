package com.bezkoder.spring.security.postgresql.payload.request;

import javax.validation.constraints.NotBlank;

public class QuestionRequest {
    @NotBlank
    private String title;

    @NotBlank
    private String content;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
