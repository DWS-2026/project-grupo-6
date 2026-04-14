package com.example.projectgrupo6.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO (
        Long id,
        LocalDateTime orderDate,
        Double totalAmount,
        String status,
        UserDTO user,
        List<OrderItemDTO> items
) {}
