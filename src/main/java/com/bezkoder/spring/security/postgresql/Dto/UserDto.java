package com.bezkoder.spring.security.postgresql.Dto;

import com.bezkoder.spring.security.postgresql.models.Badge;
import com.bezkoder.spring.security.postgresql.models.Reputation;
import com.bezkoder.spring.security.postgresql.models.Role;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Getter
@Setter
public class UserDto {
    private Long matricul;
    private String email;
    private String nom;
    private String Username ;

    public Reputation getReputation() {
        return reputation;
    }

    public void setReputation(Reputation reputation) {
        this.reputation = reputation;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    private Reputation reputation;
    private  byte[] image;






    public List<String> getRoles() {
        return roles;
    }


    private String prenom;
    private List<String> roles;

    private String imageBase64;

    // Getters and setters for imageBase64

    public String getImageBase64() {
        return imageBase64;
    }

    public void setImageBase64(String imageBase64) {
        this.imageBase64 = imageBase64;
    }



}
