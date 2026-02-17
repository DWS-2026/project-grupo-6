package com.example.projectgrupo6.domain;

import java.util.ArrayList;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class Comment {
    

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String content;

    private String author;

    @ManyToOne
    private Product owner;
    @ManyToOne
    private Product product;

    // Constructors
    public Comment() {} 

    public Comment(String content, String author, Product owner, Product product) {
        this.content = content;
        this.author = author;
        this.owner = owner;
        this.product = product;
    }

    // Getters and Setters
    public Long getId() {
        return id;  
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Product getOwner() {
        return owner;
    }

    public void setOwner(Product owner) {
        this.owner = owner;
    }

    public Product getProduct() {
        return product;
    }
    

}
