package com.bezkoder.spring.security.postgresql.controllers;

import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.User;
import com.bezkoder.spring.security.postgresql.service.BadgeService;
import com.bezkoder.spring.security.postgresql.service.UserService;
import com.bezkoder.spring.security.postgresql.service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/badges")
public class BadgeController {
    private final BadgeService badgeService;
    private final UserServiceImp userService;

    @Autowired
    public BadgeController(BadgeService badgeService,UserServiceImp userService) {
        this.badgeService = badgeService;
        this.userService = userService;
    }
    @GetMapping("/getAll")
    public List<Badge> getAllBadges() {
        return badgeService.getAllBadges();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Badge> getBadgeById(@PathVariable Long id) {
        Badge badge = badgeService.getBadgeById(id);
        if (badge != null) {
            return ResponseEntity.ok(badge);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/create")
    public ResponseEntity<Badge> createBadge(@RequestBody Badge badge) {
        // Get the currently authenticated user
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();

        // Get the User object from the database
        User user = userService.getUserByUsername(username);

        // Add the badge to the user and save the user
        userService.addBadgeToUser(user.getMatricule(), badge);

        return ResponseEntity.ok().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadge(@PathVariable Long id) {
        Badge existingBadge = badgeService.getBadgeById(id);
        if (existingBadge != null) {
            badgeService.deleteBadge(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}



