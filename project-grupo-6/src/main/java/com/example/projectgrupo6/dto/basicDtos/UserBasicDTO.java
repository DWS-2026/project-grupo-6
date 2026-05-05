package com.example.projectgrupo6.dto.basicDtos;

import java.util.List;

public record UserBasicDTO (
        Long id,
        String username,
        String firstname,
        String lastname,
        String email,
        List<String> roles
) {}
