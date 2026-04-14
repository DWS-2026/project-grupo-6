package com.example.projectgrupo6.dto.basicDtos;

import java.util.List;

public record ProductBasicDTO (
        Long id,
        String name,
        String description,
        Double price,
        String category,
        String powerSource,
        String brand,
        List<String> colors,
        int reviewCount,
        int stock,
        String specification
        //images (?)
) {}
