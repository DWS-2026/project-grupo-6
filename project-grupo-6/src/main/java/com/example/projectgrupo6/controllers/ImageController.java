package com.example.projectgrupo6.controllers;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.repositories.ProductRepository;
import com.example.projectgrupo6.services.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.SQLException;

@Controller
public class ImageController {
    @Autowired
    private ImageService imageService;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/{id}/image/{index}")
    public ResponseEntity<Object> getImageFile(@PathVariable long id, @PathVariable int index) throws SQLException {

        Resource imageFile = imageService.getImageFile(id, index); //multiple images

        MediaType mediaType = MediaTypeFactory
                .getMediaType(imageFile)
                .orElse(MediaType.IMAGE_JPEG);

        return ResponseEntity
                .ok()
                .contentType(mediaType)
                .body(imageFile);
    }

    @GetMapping("/{id}/documentation")
    public ResponseEntity<Resource> getDocumentation(@PathVariable long id) throws SQLException {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getDocumentation() == null) {
            throw new RuntimeException("No documentation available");
        }

        Resource resource = new InputStreamResource(
                product.getDocumentation().getBinaryStream()
        );

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"documentation.pdf\"")
                .body(resource);
    }
    
}
