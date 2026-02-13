package com.example.projectgrupo6.domain;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.List;

@Component
@SessionScope
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long Id;

    private String firstname;
    private String lastname;
    private String username;
    private String email;
    private String password;

    /*
    @OneToMany
    private List<Order> order;

    @OneToMany
    private List<Review> review;
    */

    //Constructor
    public User(Long id, String firtsname, String lastname, String username, String email, String password) {
        Id = id;
        this.firstname = firtsname;
        this.lastname = lastname;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public User(){}
}
