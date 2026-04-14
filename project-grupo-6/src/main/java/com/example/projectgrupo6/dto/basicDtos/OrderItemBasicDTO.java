package com.example.projectgrupo6.dto.basicDtos;

public record OrderItemBasicDTO (
        Long id,
        int quantity,
        ProductBasicDTO product,
        Double priceAtPurchase
) {}
