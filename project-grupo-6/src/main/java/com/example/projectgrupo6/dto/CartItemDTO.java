package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;

public record CartItemDTO (
        Long id,
        CartDTO cart,
        ProductBasicDTO product,
        int quantity
) {}
