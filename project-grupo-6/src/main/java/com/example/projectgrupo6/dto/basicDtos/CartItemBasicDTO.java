package com.example.projectgrupo6.dto.basicDtos;

public record CartItemBasicDTO (
        Long id,
        ProductBasicDTO product,
        int quantity
) {}
