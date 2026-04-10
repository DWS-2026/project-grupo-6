package com.example.projectgrupo6.dto;

public record OrderItemDTO (
        Long id,
        OrderDTO order,
        ProductDTO product,
        int quantity,
        Double priceAtPurchase
) {}
