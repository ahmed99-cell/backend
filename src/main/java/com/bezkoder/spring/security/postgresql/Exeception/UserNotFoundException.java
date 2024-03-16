package com.bezkoder.spring.security.postgresql.Exeception;

import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class UserNotFoundException extends UsernameNotFoundException {
    public UserNotFoundException(Long matricule){
        super("Could not found the user with matricule " + matricule);
    }
}
