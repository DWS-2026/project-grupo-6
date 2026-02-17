package com.example.projectgrupo6.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name; 
    
    @Column(length = 2000)
    private String description; 

    private Double price; 

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> images = new ArrayList<>();

    private String category; 

    private String powerSource; 

    private String brand;

    private List<String> colors = new ArrayList<>();

    private int reviewCount; 
    private int stock;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL)
    private List<Comment> comments;

    //add documentation on .pdf file

    // Constructors
    public Product() {}

    public Product(String name, String description, Double price, List<String> images, String category, String powerSource, String brand, List<String> colors, int reviewCount, int stock, List<Comment> comments) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.images = images;
        this.category = category;
        this.powerSource = powerSource;
        this.brand = brand;
        this.colors = colors;
        this.reviewCount = reviewCount;
        this.stock = stock;
        this.comments = comments;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }               
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getBrand() {
        return brand;
    }
    public void setBrand(String brand) {
        this.brand = brand;
    }
    public List<String> getColors() {
        return colors;
    }
    public void setColors(List<String> colors) {
        this.colors = colors;
    }
    public void addColor(String color) {
        this.colors.add(color);
    }
    public void removeColor(String color) {
        this.colors.remove(color);
    }
    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getMainImage() {
        if (images != null && !images.isEmpty()) {
            return images.get(0); 
        }
        return "/css/img/default-product.png"; 
    }

    public String getPowerSource() {
        return powerSource;
    }

    public void setPowerSource(String powerSource) {
        this.powerSource = powerSource;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }
    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

}