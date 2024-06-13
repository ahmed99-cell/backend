package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.ERole;
import com.bezkoder.spring.security.postgresql.models.Role;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.BadgeRepository;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    PasswordEncoder encoder;


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());

    }
    public UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setMatricul(user.getMatricule());
        userDto.setEmail(user.getEmail());
        userDto.setNom(user.getNom());
        userDto.setUsername(user.getUsername());
        userDto.setPrenom(user.getPrenom());
        userDto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        return userDto;
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

            // Encrypt the new password before saving it
            String encryptedPassword = encoder.encode(newUser.getPassword());
            user.setPassword(encryptedPassword);

            // Clear existing roles and add new roles


            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(matricule));
    }
    @Override
    public void addBadgeToUser(Long userId, Badge badge) {
        badge.setName("Nom du badge");
        badge.setDescription("Description du badge");
        badge.setIconUrl("URL de l'icône du badge");

        badge = badgeRepository.save(badge);

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.getBadges().add(badge);

            userRepository.save(user);
        }
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    @Override
    public void updateUserRole(Long userId, String newRoleName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));

        Set<Role> updatedRoles = new HashSet<>();
        Role newRole = roleRepository.findByName(ERole.valueOf(newRoleName))
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));

        updatedRoles.add(newRole);

        user.setRoles(updatedRoles);
        userRepository.save(user);
    }

    }




