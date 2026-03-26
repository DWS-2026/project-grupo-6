package com.example.projectgrupo6.domain;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;


import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String encodedPassword;

    @Lob
    private Blob profileImage;

    //Change when ready
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;

    @OneToMany(mappedBy = "owner",cascade = CascadeType.ALL)
    private List<Comment> review = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Order> orders;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cart cart;

    //Constructor
    public User(Long id, String firstname, String lastname, String username, String email, String encodedPassword, Blob profileImage, String rol, List<Comment> review) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.encodedPassword = encodedPassword;
        this.profileImage = profileImage;
        this.setRol(rol); 
        this.review = review;
    }

    public User(){}

    //Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }
    public void setLastname(String lastname) {
        this.lastname = lastname;
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

    public String getEncodedPassword() {
        return encodedPassword;
    }
    public void setEncodedPassword(String encodedPassword) {
        this.encodedPassword = encodedPassword;
    }

    public Blob getProfileImage() {
        return profileImage;
    }
    public void setProfileImage(Blob profileImage) {
        this.profileImage = profileImage;
    }

    public List<String> getRoles() {return roles;}
    public void setRoles(List<String> roles) {this.roles = roles;}

    public List<Order> getOrders() {
        return orders;
    }
    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
    public String getRol() {
        return (this.roles != null && !this.roles.isEmpty()) ? this.roles.get(0) : "User";
    }

    public void setRol(String rol) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.clear();
        if (rol != null && !rol.isEmpty()) {
            this.roles.add(rol);
        }
    }
    public void addReview(Comment comment) {
        this.review.add(comment);
    }
    public void removeReview(Comment comment) {
        this.review.remove(comment);
    }
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    //To String:
    @Override
    public String toString() {
        return "User{" +
                "Id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", encodedPassword='" + encodedPassword + '\'' +
                ", profileImage='" + profileImage + '\'' +
                '}';
    }
}
