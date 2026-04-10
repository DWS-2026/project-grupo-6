package com.example.projectgrupo6.dto;

public record ProductDTO(
        Long id,
        String name,
        String description,
        Double price,
        String category,
        String powerSource,
        String brand,
        int stock
) {}
