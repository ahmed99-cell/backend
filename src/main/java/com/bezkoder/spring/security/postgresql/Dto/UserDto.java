package com.bezkoder.spring.security.postgresql.Dto;

import com.bezkoder.spring.security.postgresql.models.Role;

import java.util.List;

public class UserDto {
    private Long matricul;
    private String email;
    private String nom;

    public Long getMatricul() {
        return matricul;
    }

    public void setMatricul(Long matricul) {
        this.matricul = matricul;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    private String prenom;
    private List<String> roles;


}
