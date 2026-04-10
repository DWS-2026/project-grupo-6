package com.example.projectgrupo6.controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;


import com.example.projectgrupo6.domain.Product;
import com.example.projectgrupo6.domain.User;
import com.example.projectgrupo6.services.ImageService;
import com.example.projectgrupo6.services.ProductService;
import com.example.projectgrupo6.services.UserService;

import jakarta.servlet.http.HttpSession; 

@Controller 
@RequestMapping("/product")
public class ProductController {

    @Autowired 
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/add")
    public String renderProductForm(HttpSession session, Model model) {

        model.addAttribute("isAdmin", true); 
        model.addAttribute("isEdit", false);
        model.addAttribute("actionUrl", "/product/add");
        //For Mustache, it must have an object even if it's empty
        model.addAttribute("product", new Product());

        return "product-form";
    }

    //change
    @PostMapping("/add")
    public String addProduct(
            @RequestParam String name,
            @RequestParam String brand,
            @RequestParam Double price, 
            @RequestParam String category,
            @RequestParam String powerSource,
            @RequestParam(required = false) List<String> colors,
            @RequestParam String description,
            @RequestParam int stock,
            @RequestParam(required = false) String specification,
            @RequestParam(value = "images", required = false) MultipartFile[] imageFiles,
            RedirectAttributes attributes 
    ) {
        
        // --- SECURITY VALIDATION OF BACKEND ---
        long validImagesCount = productService.checkNumberImages(imageFiles);
        
        if (validImagesCount > 4) {
            attributes.addFlashAttribute("errorMessage", "Security Error: Maximum 4 images allowed.");
            return "redirect:/product/add";
        }

        Product p = new Product();
        productService.setAttbProduct(p, name, brand, price, category, powerSource, description, specification);
        p.setColors(colors != null ? colors : new ArrayList<>());
        p.setStock(stock);
        p.setReviewCount(0);

        List<java.sql.Blob> productImages = new ArrayList<>();
        
        try {
            // 1. Images logic
            if (imageFiles != null && imageFiles.length > 0) {
                for (MultipartFile file : imageFiles) {
                    if (!file.isEmpty()) {
                        byte[] bytes = file.getBytes();
                        java.sql.Blob blob = new javax.sql.rowset.serial.SerialBlob(bytes);
                        productImages.add(blob);
                    }
                }
            }
            
            if (productImages.isEmpty()) {
                productImages.add(imageService.loadImage("default-product.png"));
            }
            p.setImages(productImages);

        } catch (Exception e) {
            e.printStackTrace();
            attributes.addFlashAttribute("errorMessage", "Error processing the files. Please try again.");
            return "redirect:/product/add";
        }

        productService.save(p); 
        return "redirect:/admin/products"; 
    }
}