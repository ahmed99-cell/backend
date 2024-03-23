package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImp implements UserService{
    @Autowired
    private UserRepository userRepository;


    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long matricule) {
        return userRepository.findById(matricule)
                .orElseThrow(() -> new UserNotFoundException(matricule));
    }

    @Override
    public void deleteUser(Long matricule) {
        if (!userRepository.existsById(matricule)) {
            throw new UserNotFoundException(matricule);
        }
        userRepository.deleteById(matricule);

    }

    @Override
    public User updateUser(User newUser, Long matricule) {
        return userRepository.findById(matricule).map(user -> {
            user.setUsername(newUser.getUsername());
            user.setNom(newUser.getNom());
            user.setPrenom(newUser.getPrenom());
            user.setEmail(newUser.getEmail());
            user.setPassword(newUser.getPassword());
            user.setRoles(newUser.getRoles());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(matricule));

    }
}
