package com.example.projectgrupo6.dto;

import java.util.List;

public record ProductDTO(
        Long id,
        String name,
        String description,
        Double price,

        List<ImageDTO> images,
        String category,
        String powerSource,
        String brand,
        List<String> colors,
        int reviewCount,
        int stock,
        String specification,
        List<CommentDTO> comments
) {}
