package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.request.UserRequest;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface UserService {

    List<UserDto> getAllUsers();
    User getUserById(Long matricule);
    void deleteUser(Long matricule);
    User updateUser(User newUser, Long matricule, MultipartFile image);
    void checkAndUpdateBadge(User user);
    void increaseReputation(Long userId);
    public void updateReputationUserId(Long reputationId, Long userId);
    void updateUserRole(Long userId, String newRoleName);

}
