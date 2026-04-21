package com.example.projectgrupo6.dto.basicDtos;

import java.time.LocalDateTime;

public record OrderBasicDTO (
        Long id,
        LocalDateTime orderDate,
        Double totalAmount,
        String status
) {}
