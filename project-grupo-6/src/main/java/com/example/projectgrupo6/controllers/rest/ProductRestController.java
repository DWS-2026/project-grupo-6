package com.example.projectgrupo6.controllers.rest;

import com.example.projectgrupo6.dto.ProductDTO;
import com.example.projectgrupo6.dto.basicDtos.ProductBasicDTO;
import com.example.projectgrupo6.dto.mappers.ProductMapper;
import com.example.projectgrupo6.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RequestMapping("/api/v1/products")
@RestController
public class ProductRestController {
    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductService productService;

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

    //PUT

    //DELETE
    @DeleteMapping("/{id}")
    public void deleteProduct (@PathVariable long id){
        productService.delete(id);
    }

}
