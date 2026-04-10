package com.example.projectgrupo6.dto;

import java.util.List;

public record CartDTO (
       Long id,
       UserDTO user,
       List<CartItemDTO> items
){}
