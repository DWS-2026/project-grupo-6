package com.example.projectgrupo6.dto;

import com.example.projectgrupo6.domain.Image;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}
