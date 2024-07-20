package com.bezkoder.spring.security.postgresql.Exeception;

public class AnswerNotFoundException extends RuntimeException {
    public AnswerNotFoundException(Long answerId) {
        super("Answer not found with id: " + answerId);
    }
}