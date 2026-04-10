package com.example.projectgrupo6.dto;

public record CartItemDTO (
      Long id,
      CartDTO cart,
      ProductDTO product,
      int quantity
) {}
