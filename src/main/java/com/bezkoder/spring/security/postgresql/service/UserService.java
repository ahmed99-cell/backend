package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.models.User;

import java.util.List;

public interface UserService {

    List<User> getAllUsers();
    User getUserById(Long matricule);
    void deleteUser(Long matricule);
    User updateUser(User newUser, Long matricule);
}
