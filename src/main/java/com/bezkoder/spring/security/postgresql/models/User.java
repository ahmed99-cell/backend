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


@Entity
@Table( name = "users",
        uniqueConstraints = {
                // @UniqueConstraint(columnNames = "username"),
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
  @OneToMany(mappedBy = "sender")
  @JsonIgnore
  private Set<Message> sentMessages = new HashSet<>();

  @OneToMany(mappedBy = "receiver")
  @JsonIgnore
  private Set<Message> receivedMessages = new HashSet<>();
  @ManyToMany
  @JoinTable(
          name = "user_badges",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "badge_id"))
  @JsonIgnore
  private Set<Badge> badges;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @JsonManagedReference
  private Reputation reputation;
  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Notification> notifications;

  public Set<Vote> getVotes() {
    return votes;
  }

  public void setVotes(Set<Vote> votes) {
    this.votes = votes;
  }

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private Set<Vote> votes = new HashSet<>();

  public List<Notification> getNotifications() {
    return notifications;
  }

  public void setNotifications(List<Notification> notifications) {
    this.notifications = notifications;
  }

  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
  @JsonIgnore
  private Set<Role> roles = new HashSet<>();


  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonIgnore
  private Set<Question> questions = new HashSet<>();

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @JsonIgnore
  private Set<Answer> answers = new HashSet<>();

  public List<Favorite> getFavorites() {
    return favorites;
  }

  public void setFavorites(List<Favorite> favorites) {
    this.favorites = favorites;
  }

  @OneToMany(mappedBy = "user")
  @JsonIgnore
  private List<Favorite> favorites;

  public Set<Badge> getBadges() {
    return badges;
  }

  public void setBadges(Set<Badge> badges) {
    this.badges = badges;
  }

  public Reputation getReputation() {
    return reputation;
  }

  public void setReputation(Reputation reputation) {
    this.reputation = reputation;
  }

  public Set<Message> getSentMessages() {
    return sentMessages;
  }

  public void setSentMessages(Set<Message> sentMessages) {
    this.sentMessages = sentMessages;
  }

  public Set<Message> getReceivedMessages() {
    return receivedMessages;
  }

  public void setReceivedMessages(Set<Message> receivedMessages) {
    this.receivedMessages = receivedMessages;
  }

  public Set<Question> getQuestions() {
    return questions;
  }

  public void setQuestions(Set<Question> questions) {
    this.questions = questions;
  }

  public Set<Answer> getAnswers() {
    return answers;
  }

  public void setAnswers(Set<Answer> answers) {
    this.answers = answers;
  }






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
    this.matricule = matricule;
  }

  public String getPrenom() {
    return prenom;
  }

  public Long getMatricule() {
    return matricule;
  }

  public byte[] getImage() {
    return image;
  }

  public void setImage(byte[] image) {
    this.image = image;
  }

  @Lob
  private  byte[] image;
  @Transient
  private String imageBase64;

  // Getters and setters for imageBase64

  public String getImageBase64() {
    return imageBase64;
  }

  public void setImageBase64(String imageBase64) {
    this.imageBase64 = imageBase64;
  }
}
