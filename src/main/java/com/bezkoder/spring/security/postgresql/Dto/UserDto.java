package com.bezkoder.spring.security.postgresql.Dto;

import com.bezkoder.spring.security.postgresql.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class UserDto {
    private Long matricul;
    private String email;
    private String nom;
    private String Username ;






    public List<String> getRoles() {
        return roles;
    }


    private String prenom;
    private List<String> roles;


}
