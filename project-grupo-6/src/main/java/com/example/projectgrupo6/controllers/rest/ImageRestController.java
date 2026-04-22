package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.mappers.ImageMapper;
import com.example.projectgrupo6.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/images")
@RestController
public class ImageRestController {
    @Autowired
    private ImageService imageService;
    @Autowired
    private ImageMapper imageMapper;

    @GetMapping("/{id}")
    public ImageDTO getImage(@PathVariable long id){
        return imageMapper.toDTO(imageService.getImage(id));
    }
}
