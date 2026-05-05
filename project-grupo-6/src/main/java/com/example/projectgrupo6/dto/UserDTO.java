package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.CommentBasicDTO;

import java.util.List;

public record UserDTO(
        Long id,
        String firstname,
        String lastname,
        String email,
        String username,
        //password not necessary
        ImageDTO profileImage,

        //necessary (?)
        List<String> roles,
        List<CommentBasicDTO> review,
        List<OrderDTO> orders,
        CartDTO cart
) {}
