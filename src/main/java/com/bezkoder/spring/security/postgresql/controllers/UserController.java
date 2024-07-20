package com.bezkoder.spring.security.postgresql.controllers;


import com.bezkoder.spring.security.postgresql.Dto.RoleUpdateRequest;
import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.request.UserRequest;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder encoder;


    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @DeleteMapping("/user/{id}")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return "User with ID " + id + " has been deleted successfully.";
    }

    // Dans UserController.java
    @PutMapping("/user/{id}")
    public ResponseEntity<?> updateUser(@RequestParam("user") String userString, @RequestParam("image") MultipartFile image, @PathVariable Long id) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            User newUser = objectMapper.readValue(userString, User.class);
            User updatedUser = userService.updateUser(newUser, id, image);
            return ResponseEntity.ok(updatedUser);
        } catch (JsonProcessingException e) {
            return ResponseEntity.badRequest().body("Invalid user data: " + e.getMessage());
        }
    }

    private User convertToUser(UserRequest userRequest) {
        User user = new User();
        user.setUsername(userRequest.getUsername());
        user.setNom(userRequest.getNom());
        user.setPrenom(userRequest.getPrenom());
        user.setEmail(userRequest.getEmail());
        String encryptedPassword = encoder.encode(userRequest.getPassword());
        user.setPassword(encryptedPassword);
        return user;
    }







    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable(value = "id") Long userId, @RequestBody RoleUpdateRequest request) {
        userService.updateUserRole(userId, request.getNewRoleName());
        return ResponseEntity.ok().body("User role updated successfully");
    }

  
}
