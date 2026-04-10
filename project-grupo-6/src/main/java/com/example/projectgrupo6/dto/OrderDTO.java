package com.example.projectgrupo6.dto;

import java.util.List;

public record OrderDTO (
        Long id,

        //orderDate (?)
        Double totalAmount,
        String status,
        UserDTO user,
        List<OrderItemDTO> items
) {}
