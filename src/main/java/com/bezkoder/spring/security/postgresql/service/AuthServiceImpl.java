package com.bezkoder.spring.security.postgresql.service;

import com.bezkoder.spring.security.postgresql.config.ResetPasswordRequest;
import com.bezkoder.spring.security.postgresql.models.ERole;
import com.bezkoder.spring.security.postgresql.models.Role;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.payload.request.LoginRequest;
import com.bezkoder.spring.security.postgresql.payload.request.SignupRequest;
import com.bezkoder.spring.security.postgresql.payload.response.JwtResponse;
import com.bezkoder.spring.security.postgresql.payload.response.MessageResponse;
import com.bezkoder.spring.security.postgresql.repository.RoleRepository;
import com.bezkoder.spring.security.postgresql.repository.UserRepository;
import com.bezkoder.spring.security.postgresql.security.jwt.JwtUtils;
import com.bezkoder.spring.security.postgresql.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${mail.sender.email}") // Injection de mailSenderEmail à partir du fichier de propriétés
    private String mailSenderEmail;

    @Autowired

    public AuthServiceImpl( JavaMailSender javaMailSender, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder encoder, JwtUtils jwtUtils) {

        this.javaMailSender = javaMailSender;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;

    }





    @Override
    public ResponseEntity<?> authenticateUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
                .collect(Collectors.toList());

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getMatricule(), userDetails.getUsername(), userDetails.getEmail(), roles));
    }

    @Override
    public ResponseEntity<?> resetPassword(ResetPasswordRequest resetPasswordRequest) {
        Optional<User> userOptional = userRepository.findByEmail(resetPasswordRequest.getEmail());
        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: User not found for the provided email"));
        }

        User user = userOptional.get();

        if (!encoder.matches(resetPasswordRequest.getOldPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Incorrect old password"));
        }

        user.setPassword(encoder.encode(resetPasswordRequest.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("Password reset successfully."));
    }

    @Override
    public ResponseEntity<?> registerUser(SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }
        String randomPassword = generateRandomPassword();
        String plainPassword = randomPassword;

        sendPasswordByEmail(signUpRequest.getEmail(), plainPassword, mailSenderEmail);
        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), signUpRequest.getNom(), signUpRequest.getPrenom(), encoder.encode(randomPassword));

        Set<String> strRoles = signUpRequest.getRole();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null) {
            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "mod":
                        Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(modRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }

        user.setRoles(roles);
        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }
    private String generateRandomPassword() {
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        return randomPassword;
    }
    @Async

    public void sendPasswordByEmail(String recipientEmail, String plainPassword, String mailSenderEmail) {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, "utf-8");

        try {
            helper.setTo(recipientEmail);
            helper.setFrom(mailSenderEmail);
            helper.setSubject("Votre mot de passe pour l'inscription");
            helper.setText("Votre mot de passe est : " + plainPassword, true);
            javaMailSender.send(message);

            System.out.println("E-mail envoyé avec succès à : " + recipientEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'envoi de l'e-mail à : " + recipientEmail + ". Cause : " + e.getMessage());
        }
    }


}
