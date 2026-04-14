package com.example.projectgrupo6.dto.basicDtos;
import com.example.projectgrupo6.dto.ProductCartDTO;

public record CartItemBasicDTO (
        Long id,
        ProductCartDTO product,
        int quantity
) {}
