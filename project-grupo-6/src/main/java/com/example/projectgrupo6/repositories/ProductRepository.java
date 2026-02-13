package com.example.projectgrupo6.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.projectgrupo6.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    // Filters based of shop.html
    List<Product> findByCategory(String category);
    List<Product> findByPowerSource(String powerSource);
    List<Product> findByBrand(String brand);
}