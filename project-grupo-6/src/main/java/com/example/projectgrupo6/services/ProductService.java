package com.example.projectgrupo6.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.repositories.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository repository;

    public List<Product> getAllProducts() {
        return repository.findAll();
    }

    public List<Product> filterByPowerSource(String source) {
        return repository.findByPowerSource(source);
    }

    public List<Product> filterByCategory(String category) {
        return repository.findByCategory(category);
    }

    public Optional<Product> getById(Long id) {
        return repository.findById(id);
    }

    public void save(Product product) {
        repository.save(product);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Product> getThreeRandomProducts() {
        List<Product> allProducts = repository.findAll();
        
        List<Product> randomList = new ArrayList<>(allProducts);
        
        Collections.shuffle(randomList);
        
        return randomList.subList(0, Math.min(3, randomList.size()));
    }
}