package com.bezkoder.spring.security.postgresql.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@Setter
@Entity
@Table( name = "users",
        uniqueConstraints = {@UniqueConstraint(columnNames = "email")
        })
public class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long matricule;
  @NotBlank
  @Size(max = 20)
  private String nom;
  @NotBlank
  @Size(max = 20)
  private String prenom;



  @NotBlank
  @Size(max = 20)
  private String username;

  @NotBlank
  @Size(max = 50)
  @Email
  private String email;

  @NotBlank
  @Size(max = 120)
  private String password;
  @OneToMany(mappedBy = "sender")
  private Set<Message> sentMessages = new HashSet<>();

  @OneToMany(mappedBy = "receiver")
  private Set<Message> receivedMessages = new HashSet<>();
  @ManyToMany
  @JoinTable(
          name = "user_badges",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "badge_id"))
  private Set<Badge> badges;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonManagedReference
  private Reputation reputation;
  @OneToMany(mappedBy = "user")
  private List<Notification> notifications;

  

  @OneToMany(mappedBy = "user")
  private Set<Vote> votes = new HashSet<>();

  

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonIgnore
  private Set<Question> questions = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Answer> answers = new HashSet<>();

  

  @OneToMany(mappedBy = "user")
  private List<Favorite> favorites;







  public User() {
  }

  public User(String username, String email, String nom,String prenom,String password) {
    this.username = username;
    this.email = email;
    this.nom= nom;
    this.prenom=prenom;
    this.password = password;

  }

}
