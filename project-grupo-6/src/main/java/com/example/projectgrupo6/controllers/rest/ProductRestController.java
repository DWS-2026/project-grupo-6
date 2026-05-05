package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.domain.Image;
import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.dto.ImageDTO;
import com.example.projectgrupo6.dto.ProductDTO;
import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;
import com.example.projectgrupo6.dto.mappers.ImageMapper;
import com.example.projectgrupo6.dto.mappers.ProductMapper;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.ValidationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.NoSuchElementException;

import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

@RequestMapping("/api/v1/products")
@RestController
public class ProductRestController {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ImageMapper imageMapper;
    
    @Autowired
    private ProductService productService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ValidationService validationService;

    //GET
    @GetMapping("/")
    public Page<ProductBasicDTO> getAllBasicProducts (Pageable pageable){
        return (productService.getAll(pageable).map(productMapper::toBasicDTO));
    }

    @GetMapping("/{id}")
    public ProductDTO getProduct (@PathVariable long id){
        return productMapper.toDTO(productService.getById(id).orElseThrow());
    }

    //POST
    //Product
    @PostMapping("/")
    public ResponseEntity<ProductBasicDTO> createProduct (@RequestBody ProductBasicDTO productBasicDTO){
        validationService.validateProduct(productBasicDTO.name(), productBasicDTO.description(), productBasicDTO.price(), null);
        
        Product prod = productMapper.toDomainFromBasic(productBasicDTO);
        productService.save(prod);
        
        URI location = fromCurrentRequest().path("/{id}").buildAndExpand(prod.getId()).toUri();
        return ResponseEntity.created(location).body(productMapper.toBasicDTO(prod));
    }

    //Image
    //Only put needed ? It changes the images either there's prior images or not.
//    @PostMapping("/{id}/images")
//    public ResponseEntity<ImageDTO> createImage (@PathVariable long id, @RequestParam MultipartFile imageFile) throws IOException{
//        if (imageFile.isEmpty()) {
//            throw new IllegalArgumentException("Image file cannot be empty");
//        }
//
//        Image image = imageService.createImage(imageFile);
//        productService.addImageToProduct(id, image);
//        URI location = fromCurrentContextPath()
//                .path("/images/{imageId}/media")
//                .buildAndExpand(image.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(imageMapper.toDTO(image));
//    }
    //

    //PUT
    //Product
    @PutMapping("/{id}")
    public ProductDTO changeProduct (@PathVariable long id, @RequestBody ProductDTO updateDTO){
        if(productService.getById(id).isPresent()){
            validationService.validateProduct(updateDTO.name(), updateDTO.description(), updateDTO.price(), null);
            
            Product updatedProd = productMapper.toDomain(updateDTO);
            updatedProd.setId(id);
            productService.save(updatedProd);
            return productMapper.toDTO(updatedProd);
        } else {
            throw new NoSuchElementException();
        }
    }

    //Image or images of the product
    //Doesn't change in web
    //
    @PutMapping("/{id}/images")
    public ResponseEntity<Object> replaceImageFile(@PathVariable long id,
                                                   @RequestParam MultipartFile[] imageFile) throws IOException {
        productService.setProductImages(productService.getById(id).orElseThrow(), imageFile);
        return ResponseEntity.noContent().build();
    }
    //

    //DELETE
    //Product
    @DeleteMapping("/{id}")
    public ResponseEntity<ProductBasicDTO> deleteProduct (@PathVariable long id){
        ProductBasicDTO prod = productMapper.toBasicDTO(productService.getById(id).orElseThrow());
        productService.delete(id);
        return ResponseEntity.ok(prod);
    }

    //Image
    // Changes not shown in Web -> CORRECT this
    // Only delete one image or all, one or two methods ?
    //
    @DeleteMapping("/{id}/image/{imageId}")
    public ImageDTO deleteImage (@PathVariable long id, @PathVariable long imageId) throws IOException {
        Product prod = productService.getById(id).orElseThrow();
        productService.deleteImage(id, imageId);

        Image image = new Image();
        image.setImageFile(prod.getImages().get((int)imageId));
        return imageMapper.toDTO(image);
    }
    //
}
