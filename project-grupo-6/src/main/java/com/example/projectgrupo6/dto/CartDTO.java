package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.CartItemBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;

import java.util.List;

public record CartDTO (
        Long id,
        UserBasicDTO user,
        List<CartItemBasicDTO> items
){}
