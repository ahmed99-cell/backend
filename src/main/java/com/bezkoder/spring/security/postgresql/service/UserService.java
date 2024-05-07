package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.User;

import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDto> getAllUsers();
    User getUserById(Long matricule);
    void deleteUser(Long matricule);
    User updateUser(User newUser, Long matricule);
    void addBadgeToUser(Long userId, Badge badge);
    User getUserByUsername(String username);
    void updateUserRole(Long userId, String newRoleName);

}
