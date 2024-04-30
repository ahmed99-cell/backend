package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.Dto.UserDto;
import com.bezkoder.spring.security.postgresql.Exeception.UserNotFoundException;
import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.Role;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.repository.BadgeRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImp implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BadgeRepository badgeRepository;


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
            user.setPassword(newUser.getPassword());
            user.setRoles(newUser.getRoles());
            return userRepository.save(user);
        }).orElseThrow(() -> new UserNotFoundException(matricule));

    }
    @Override
    public void addBadgeToUser(Long userId, Badge badge) {
        // Créer un nouveau badge
        badge.setName("Nom du badge");
        badge.setDescription("Description du badge");
        badge.setIconUrl("URL de l'icône du badge");

        // Sauvegarder le badge dans la base de données
        badge = badgeRepository.save(badge);

        // Récupérer un utilisateur
        User user = userRepository.findById(userId).orElse(null);

        if (user != null) {
            // Ajouter le badge à la collection de badges de l'utilisateur
            user.getBadges().add(badge);

            // Sauvegarder l'utilisateur dans la base de données
            userRepository.save(user);
        }
    }
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
}
