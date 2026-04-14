package com.example.projectgrupo6.dto.mappers;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.dto.ImageDTO;
import org.mapstruct.Mapper;

@Mapper (componentModel = "spring")
public interface ImageMapper {
    ImageDTO toDTO(Image image);
}
