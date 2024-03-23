package com.bezkoder.spring.security.postgresql.service;

import org.springframework.security.access.prepost.PreAuthorize;

public class TestServiceImp implements  TestService{
    @Override
    public String allAccess() {
        return "Public Content.";
    }

    @Override
    @PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public String userAccess() {
        return "User Content.";
    }

    @Override
    @PreAuthorize("hasRole('MODERATOR')")
    public String moderatorAccess() {
        return "Moderator Board.";
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
}
