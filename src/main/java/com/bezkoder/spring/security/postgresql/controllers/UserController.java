package com.bezkoder.spring.security.postgresql.controllers;


import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    UserRepository userRepository;
    @PostMapping("/user")
    User newUser(@RequestBody User newUser) {
        return userRepository.save(newUser);
    }
    @GetMapping("/users")
    List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/user/{id}")
    User getUserById(@PathVariable Long matricule) {
        return userRepository.findById(matricule)
                .orElseThrow(() -> new UserNotFoundException(matricule));
    }
    @DeleteMapping("/user/{id}")
    String deleteUser(@PathVariable Long matricule){
        if(!userRepository.existsById(matricule)){
            throw new  UserNotFoundException(matricule);

        }
        userRepository.deleteById( matricule);
        return "user with id " + matricule+ " has been deleted successfylly ";

    }
    @PutMapping("/user/{id}")
    User updateUser(@RequestBody User newUser, @PathVariable Long  matricule) {
        return userRepository.findById(matricule).map(user -> {
            user.setUsername(newUser.getUsername());
            user.setNom(newUser.getNom());
            user.setPrenom(newUser.getPrenom());
            user.setEmail(newUser.getEmail());
            user.setMatricule(newUser.getMatricule());
            user.setPassword(newUser.getPassword());
            user.setRoles(newUser.getRoles());
            return userRepository.save((user));
        }).orElseThrow(() -> new UserNotFoundException(matricule));
    }



}
