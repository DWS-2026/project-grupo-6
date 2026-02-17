package com.example.projectgrupo6.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders") // "order" es palabra reservada en SQL, mejor usar "orders"
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private LocalDateTime orderDate;

    private Double totalAmount;

    private String status; // Ejemplo: "PENDING", "SHIPPED", "DELIVERED"

    // Relación con el usuario (asumiendo que crearás una clase User)
    // Many Orders to One User
    //@ManyToOne
    //private User user;

    // Relación con los productos
    // En una versión simple, usamos ManyToMany
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
      name = "order_products", 
      joinColumns = @JoinColumn(name = "order_id"), 
      inverseJoinColumns = @JoinColumn(name = "product_id")
    )
    private List<Product> products = new ArrayList<>();

    // Constructores
    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(Double totalAmount, String status, List<Product> products) {
        this();
        this.totalAmount = totalAmount;
        this.status = status;
        this.products = products;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
    
    // Método de conveniencia para añadir productos
    public void addProduct(Product product) {
        this.products.add(product);
    }
}