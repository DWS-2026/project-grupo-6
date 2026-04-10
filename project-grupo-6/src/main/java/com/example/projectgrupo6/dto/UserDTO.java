package com.example.projectgrupo6.dto;

import java.util.List;

public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        //password not necessary
        ImageDTO profileImage,

        //necessary (?)
        List<String> roles,
        List<CommentDTO> review,
        List<OrderDTO> orders,
        CartDTO cart
) {}
