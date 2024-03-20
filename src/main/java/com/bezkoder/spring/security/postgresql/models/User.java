package com.bezkoder.spring.security.postgresql.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

//import jakarta.persistence.*;
//import jakarta.validation.constraints.Email;
//import jakarta.validation.constraints.NotBlank;
//import jakarta.validation.constraints.Size;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;


@Entity
@Table( name = "users",
        uniqueConstraints = { 
          @UniqueConstraint(columnNames = "username"),
          @UniqueConstraint(columnNames = "email") 
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


  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();
  @OneToMany(mappedBy = "sender")
  private Set<ChatMessage> sentMessages = new HashSet<>();
  @OneToMany(mappedBy = "receiver")
  private Set<ChatMessage> receivedMessages = new HashSet<>();
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Question> questions = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  private Set<Answer> answers = new HashSet<>();




  public User() {
  }

  public User(String username, String email, String nom,String prenom,String password) {
    this.username = username;
    this.email = email;
    this.nom= nom;
    this.prenom=prenom;
    this.password = password;

  }





  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Set<Role> getRoles() {
    return roles;
  }

  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  public String getNom() {
    return nom;
  }

  public void setNom(String nom) {
    this.nom = nom;
  }

  public void setPrenom(String prenom) {
    this.prenom = prenom;
  }

  public void setMatricule(Long matricule) {
    // Supprimez les espaces et définissez la valeur du champ
    this.matricule = matricule;
  }

  public String getPrenom() {
    return prenom;
  }

  public Long getMatricule() {
    return matricule;
  }
}
