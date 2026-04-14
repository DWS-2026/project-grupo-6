package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;

public record OrderItemDTO (
        Long id,
        OrderDTO order,
        ProductBasicDTO product,
        int quantity,
        Double priceAtPurchase
) {}
