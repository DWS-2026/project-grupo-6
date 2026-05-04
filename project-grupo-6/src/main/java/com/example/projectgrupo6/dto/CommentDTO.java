package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;
import com.example.projectgrupo6.dto.basicDtos.UserBasicDTO;

public record CommentDTO (
        Long id,
        String content,
        UserBasicDTO owner,
        ProductBasicDTO product
){}
