package com.example.projectgrupo6.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.services.ProductService; 

@Controller 
@RequestMapping("/product")
public class ProductController {

    @Autowired 
    private ProductService productService;

    @GetMapping("/add")
        public String redirectToCart(){
            return "product-form";
        }

    @PostMapping("/add")
    public String addProduct(
            @RequestParam String productName,
            @RequestParam String description,
            @RequestParam Double price, 
            @RequestParam(value = "colors[]", required = false) List<String> colors,
            @RequestParam String specification,
            @RequestParam int stock,
            @RequestParam(value = "images[]", required = false) MultipartFile[] imageFiles 
    ) {
        Product p = new Product();
        p.setName(productName);
        p.setDescription(description);
        p.setPrice(price);
        p.setSpecification(specification);
        p.setStock(stock);
        p.setColors(colors != null ? colors : new ArrayList<>());

        
        p.setImages(Arrays.asList("/css/img/default-product.png"));

        productService.save(p); 
        return "redirect:/shop";
    }

}