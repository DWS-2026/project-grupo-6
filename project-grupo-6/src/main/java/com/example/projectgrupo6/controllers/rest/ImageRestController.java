package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.mappers.ImageMapper;
import com.example.projectgrupo6.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.sql.SQLException;

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

    @GetMapping("/{id}/media")
    public ResponseEntity<Object> getImageFile(@PathVariable long id)
            throws SQLException, IOException {
        Resource imageFile = imageService.getImageFile(id);
        MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);
        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
    }

    //Changes not seen in Web, because we don't use Image at the Entities, make individual methods
    //CHANGE THIS
    //
    @PutMapping("/{id}/media")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
                                                   @RequestParam MultipartFile imageFile) throws IOException {
        imageService.replaceImageFile(id, imageFile.getInputStream());
        return ResponseEntity.noContent().build();
    }
    //
}
