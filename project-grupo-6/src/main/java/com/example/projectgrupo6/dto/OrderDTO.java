package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.OrderItemBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO (
        Long id,
        LocalDateTime orderDate,
        Double totalAmount,
        String status,
        UserBasicDTO user,
        List<OrderItemBasicDTO> items
) {}
