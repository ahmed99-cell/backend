package com.bezkoder.spring.security.postgresql.controllers;


import com.bezkoder.spring.security.postgresql.Dto.RoleUpdateRequest;
import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserService userService;


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

    @PutMapping("/user/{id}")
    public User updateUser(@RequestBody User newUser, @PathVariable Long id) {
        return userService.updateUser(newUser, id);
    }
    @PutMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable(value = "id") Long userId, @RequestBody RoleUpdateRequest request) {
        userService.updateUserRole(userId, request.getNewRoleName());
        return ResponseEntity.ok().body("User role updated successfully");
    }

  
}
