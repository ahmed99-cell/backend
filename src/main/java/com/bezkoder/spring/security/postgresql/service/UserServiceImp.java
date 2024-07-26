package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.Exeception.ResourceNotFoundException;
import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.*;
import com.bezkoder.spring.security.postgresql.payload.request.UserRequest;
import com.bezkoder.spring.security.postgresql.repository.BadgeRepository;
import com.bezkoder.spring.security.postgresql.repository.ReputationRepository;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
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
    @Autowired
    private ReputationRepository reputationRepository;


    @Override
    @Transactional
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
        userDto.setImage(user.getImage());

        userDto.setReputation(user.getReputation());

        if (user.getImage() != null) {
            String base64Image = Base64.getEncoder().encodeToString(user.getImage());
            userDto.setImageBase64(base64Image);
        }

        userDto.setRoles(user.getRoles().stream()
                .map(role -> role.getName().name())
                .collect(Collectors.toList()));
        return userDto;
    }


    @Override
    public User getUserById(Long matricule) {
        User user = userRepository.findById(matricule)
                .orElseThrow(() -> new UserNotFoundException(matricule));


        if (user.getImage() != null) {

            String base64Image = Base64.getEncoder().encodeToString(user.getImage());
            user.setImageBase64(base64Image);
        }

        return user;
    }

    @Override
    public void deleteUser(Long matricule) {
        if (!userRepository.existsById(matricule)) {
            throw new UserNotFoundException(matricule);
        }
        userRepository.deleteById(matricule);

    }

    @Override
    public User updateUser(User newUser, Long matricule, MultipartFile image) {
        return userRepository.findById(matricule).map(user -> {
            user.setUsername(newUser.getUsername());
            user.setNom(newUser.getNom());
            user.setPrenom(newUser.getPrenom());
            user.setEmail(newUser.getEmail());

            String encryptedPassword = encoder.encode(newUser.getPassword());
            user.setPassword(encryptedPassword);

            if (image != null && !image.isEmpty()) {
                try {
                    user.setImage(image.getBytes());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store image", e);
                }
            }


            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(matricule));
    }

    @Override
    public void updateReputationUserId(Long reputationId, Long userId) {
        Reputation reputation = reputationRepository.findById(reputationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reputation", "id", reputationId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        reputation.setUser(user);

        reputationRepository.save(reputation);
    }

    private Badge createBadge(String name) {
        Badge badge = new Badge();
        badge.setName(name);
        return badgeRepository.save(badge);
    }

    @Override
    public void checkAndUpdateBadge(User user) {
        int score = user.getReputation().getScore();
        Badge badge;
        if (score >= 60) {
            badge = badgeRepository.findByName("Gold")
                    .orElseGet(() -> createBadge("Gold"));
        } else if (score >= 40) {
            badge = badgeRepository.findByName("Silver")
                    .orElseGet(() -> createBadge("Silver"));
        } else {
            badge = badgeRepository.findByName("Bronze")
                    .orElseGet(() -> createBadge("Bronze"));
        }
        user.getBadges().clear();
        user.getBadges().add(badge);
        userRepository.save(user);
    }

    @Transactional
    public void increaseReputation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Error: User is not found."));
        Reputation reputation = user.getReputation();
        if (reputation == null) {
            reputation = new Reputation();
            reputation.setUser(user);
            user.setReputation(reputation);
        }
        reputation.setScore(reputation.getScore() + 5);
        userRepository.save(user);
        checkAndUpdateBadge(user);
    }

    /*  public void addBadgeToUser(Long userId, Badge badge) {
        badge.setName("Nom du badge");
        badge.setDescription("Description du badge");
        badge.setIconUrl("URL de l'icône du badge");

        badge = badgeRepository.save(badge);

        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            user.getBadges().add(badge);

            userRepository.save(user);
        }
    }*/
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




