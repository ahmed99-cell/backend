package com.bezkoder.spring.security.postgresql.service;


    public interface TestService {
        String allAccess();
        String userAccess();
        String moderatorAccess();
        String adminAccess();
    }


